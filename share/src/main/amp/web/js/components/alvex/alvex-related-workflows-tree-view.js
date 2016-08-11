/**
 * Copyright © 2012 ITD Systems
 *
 * This file is part of Alvex
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

// TODO FIXME - looks like they should be global to interact with jit-yc.js
var labelType, useGradients, nativeTextSupport, animate;

if (typeof Alvex == "undefined" || !Alvex)
{
	var Alvex = {};
}

/**
 * Workflow RelatedWorkflowsTreeView util
 *
 * @namespace Alvex
 * @class Alvex.RelatedWorkflowsTreeView
 */
(function()
{
	/**
	 * YUI Library aliases
	 */
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event;

	/**
	 * Alfresco Slingshot aliases
	 */
	var $html = Alfresco.util.encodeHTML,
		$combine = Alfresco.util.combinePaths,
		$siteURL = Alfresco.util.siteURL;

	/**
	 * Alvex.RelatedWorkflowsTreeView implementation
	 */
	Alvex.RelatedWorkflowsTreeView = {};
	Alvex.RelatedWorkflowsTreeView.prototype =
	{
	
		onTreeViewClick: function (layer, args)
		{
			Alfresco.util.Ajax.jsonGet(
			{
				url: $combine(Alfresco.constants.PROXY_URI, "api/alvex/related-workflows/task/", 
															this.taskId, "/tree"),
				successCallback:
				{
					fn: function(resp)
					{
						if( resp.json.data == '' )
							return;
						
						if( this.widgets.treePanel && this.options.st )
						{
							// load json data to draw initial orgchart group scheme
							this.options.st.loadJSON( this.copyTreeForJIT(resp.json.data[0]) );
							
							// compute node positions and layout
							this.options.st.compute();
							
							// optional: make a translation of the tree
							this.options.st.geom.translate(new $jit.Complex(-200, 0), "current");
							
							// emulate a click on the root node
							this.options.st.onClick(this.options.st.root);
							
							// small hack to ensure nodes sizes were really recalculated _after_ labels were displayed
							this.options.st.refresh();
							this.options.st.refresh();
							
							this.widgets.treePanel.show();
							return;
						}
						
						this.widgets.treePanel = Alfresco.util.createYUIPanel(this.id + "-treePanel");
						this.createJIT(resp.json.data);
						this.widgets.treePanel.show();
					},
					scope: this
				}
			});
		},
		
		// Create JIT canvas and fill it with data
		createJIT: function(tree)
		{
			/* Get client parameters and set rendering options - it was just copy-pasted.
			* I guess it is something great, but I have no idea how it works.
			*/
			var ua = navigator.userAgent;
			var iStuff = ua.match(/iPhone/i) || ua.match(/iPad/i);
			var typeOfCanvas = typeof HTMLCanvasElement;
			var nativeCanvasSupport = (typeOfCanvas == 'object' || typeOfCanvas == 'function');
			var textSupport = nativeCanvasSupport 
					&& (typeof document.createElement('canvas').getContext('2d').fillText == 'function');
			// It is set based on the fact that ExCanvas provides text support for IE 
			// and that as of today iPhone/iPad current text support is lame
			labelType = (!nativeCanvasSupport || (textSupport && !iStuff)) ? 'Native' : 'HTML';
			nativeTextSupport = labelType == 'Native';
			useGradients = nativeCanvasSupport;
			animate = !(iStuff || !nativeCanvasSupport);

			me = this;

			this.options.st = new $jit.ST({

				// tree orientation
				orientation: 'top',

				// levels of subtree to show, relative to the selected node
				levelsToShow: 10,

				// show the whole tree whether it fits into canvas or not
				constrained: false,

				// id of viz container element
				injectInto : this.id + '-treePanel-canvas',

				// set duration for the animation
				duration : 250,

				// set animation transition type
				transition : $jit.Trans.Quart.easeInOut,

				// set distance between node and its children
				levelDistance : 60,

				// distance from the selected node to the center of the canvas
				offsetX: 0,
				offsetY: 150,

				// enable panning
				Navigation : {
					enable : true,
					panning : true
				},

				// set node and edge styles
				// set overridable=true for styling individual
				// nodes or edges
				Node : {
					height : 40,
					width : 150,
					autoWidth : false,	// set it to auto-adapt to the label width
					autoHeight : true,	// set it to auto-adapt to the label height
					type : 'rectangle',
					align : 'center',
					color : '#ddd',
					overridable : true
				},

				Edge : {
					type : 'bezier',
					color : '#aaa',
					overridable : true
				},

				onBeforeCompute : function(node) {
					// Do smth clever here
				},

				onAfterCompute : function() {
					// Do smth clever here
				},

				// This method is called on DOM label creation.
				// We use it to assign necessary events for them
				onCreateLabel : function(label, node)
				{
					var idParts = node.id.split('$');
					label.id = idParts[0] + '$' + idParts[1];
					label.innerHTML = '<table style="height: 100%; width: 100%;" id="' + label.id + '-view-container">'
								+ '<tr><td style="height=100%;">'
								+ '<a href="/share/page/workflow-details?workflowId=' + label.id + '" ' 
								+ ' id="' + label.id + '-view">' + node.name + '</a>' 
								+ '</td></tr></table>';

					/*YAHOO.util.Event.on(label.id, 'click', me.onContainerClick, node, me);
					YAHOO.util.Event.on(label.id + '-view', 'click', me.onViewLinkClick, node, me);*/

					// set label styles
					var style = label.style;
					style.width = 150 + 'px';
					style.height = 40 + 'px';
					style.cursor = 'pointer';
					style.color = '#333';
					style.fontSize = 14 + 'px';
					style.textAlign = 'center';

				},

				// This method is called right before plotting
				// a node. It's useful for changing an individual node
				// style properties before plotting it.
				// The data properties prefixed with a dollar
				// sign will override the global node style properties.
				onBeforePlotNode : function(node)
				{
					// default node height
					var label_height = 40;

					// get height of the node label
					var label = document.getElementById(node.id + "-view-container");
					if(label && label.clientHeight > 40)
						label_height = label.clientHeight;

					// if label is too big - resize node
					if(label_height > node.data.$height)
						node.data.$height = label_height;

					var isActive = (node.id.split('$')[2] == "true");
					if( isActive )
						node.data.$color = "#EDFD3F";
					else
						node.data.$color = "#7AEE3C";
				},

				// This method is called right before plotting
				// an edge. It's useful for changing an individual edge
				// style properties before plotting it.
				// Edge data proprties prefixed with a dollar sign will
				// override the Edge global style properties.
				onBeforePlotLine : function(adj)
				{
					if (adj.nodeFrom.selected && adj.nodeTo.selected) {
						adj.data.$color = "#aaa";
						adj.data.$lineWidth = 3;
					} else {
						delete adj.data.$color;
						delete adj.data.$lineWidth;
					}
				}
			});

			// load json data to draw initial orgchart group scheme
			this.options.st.loadJSON( this.copyTreeForJIT(tree[0]) );

			// compute node positions and layout
			this.options.st.compute();

			// optional: make a translation of the tree
			this.options.st.geom.translate(new $jit.Complex(-200, 0), "current");

			// emulate a click on the root node
			this.options.st.onClick(this.options.st.root);

			// small hack to ensure nodes sizes were really recalculated _after_ labels were displayed
			this.options.st.refresh();
			this.options.st.refresh();

		},

		copyTreeForJIT: function(tree)
		{
			var jitTree = tree;
			jitTree.id += '$' + jitTree.isActive;
			if( jitTree.id == this.workflowId )
				jitTree.id += '$' + 'current';
			jitTree.name = jitTree.title;
			for(var c in tree.children)
				jitTree.children[c] = this.copyTreeForJIT(tree.children[c]);
			return jitTree;
		}
      
   }          
})();