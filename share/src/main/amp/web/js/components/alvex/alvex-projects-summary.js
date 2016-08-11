/**
 * Copyright © 2014 ITD Systems
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

// Ensure root object exists
if (typeof Alvex == "undefined" || !Alvex)
{
	var Alvex = {};
}

(function()
{
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		DDM = YAHOO.util.DragDropMgr,
		DDTarget = YAHOO.util.DDTarget,
		KeyListener = YAHOO.util.KeyListener;

	var $html = Alfresco.util.encodeHTML;

	Alvex.ProjectsSummary = function(htmlId)
	{
		Alvex.ProjectsSummary.superclass.constructor.call(this, "ProjectsSummary", htmlId);
		return this;
	};

	YAHOO.extend(Alvex.ProjectsSummary, Alfresco.component.Base,
	{
		options:
		{
		},

		onReady: function()
		{
			var YdataTable = YAHOO.widget.DataTable;
			var YdataSource = YAHOO.util.DataSource;
			var workflowsFieldFormatter = function(elLiner, oRecord, oColumn, oData)
			{
				var record = oRecord.getData();
				var now = new Date();
				var overdue = 0;
				var warning = 0;
				var everythingOk = 0;
				var withoutTime = 0;
				var completed = 0;
				var htmlR = "";
				var htmlY = "";
				var htmlG = "";
				var htmlN = "";
				for( var i = 0; i < record.workflows.length; i++ )
				{
					var m = record.workflows[i];
					if(m.status == "completed")
					{
						completed++;
						continue;
					}
					if(m.dueDate == "")
					{
						withoutTime++;
						htmlN += '<img title="' + m.summary + '" src="/share/res/components/images/gray-btn.png"  style="padding-right: 5px; width: 16px; height: 16px;" />';
						continue;
					}
					var dueDate = Alfresco.util.fromISO8601(m.dueDate);
					var daysLeft = (dueDate.getTime() - now.getTime()) / (1000*60*60*24);
					if(now > dueDate )
					{
						overdue++;
						htmlR += '<img title="' + m.summary + '" src="/share/res/components/images/red-btn.png"  style="padding-right: 5px; width: 16px; height: 16px;" />';
					}
					else if( daysLeft < 7 )
					{
						warning++;
						htmlY += '<img title="' + m.summary + '" src="/share/res/components/images/yellow-btn.png"  style="padding-right: 5px; width: 16px; height: 16px;" />';
					}
					else
					{
						everythingOk++;
						htmlG += '<img title="' + m.summary + '" src="/share/res/components/images/green-btn.png"  style="padding-right: 5px; width: 16px; height: 16px;" />';
					}
				}
				elLiner.innerHTML = htmlR + htmlY + htmlG + htmlN;
			}
			var milestonesFieldFormatter = function(elLiner, oRecord, oColumn, oData)
			{
				var record = oRecord.getData();
				var now = new Date();
				var overdue = 0;
				var warning = 0;
				var everythingOk = 0;
				var withoutTime = 0;
				var completed = 0;
				var htmlR = "";
				var htmlY = "";
				var htmlG = "";
				var htmlN = "";
				for( var i = 0; i < record.milestones.length; i++ )
				{
					var m = record.milestones[i];
					if(m.status == "completed")
					{
						completed++;
						continue;
					}
					if(m.dueDate == "")
					{
						withoutTime++;
						htmlN += '<img title="' + m.summary + '" src="/share/res/components/images/gray-btn.png"  style="padding-right: 5px; width: 16px; height: 16px;" />';
						continue;
					}
					var dueDate = Alfresco.util.fromISO8601(m.dueDate);
					var daysLeft = (dueDate.getTime() - now.getTime()) / (1000*60*60*24);
					if(now > dueDate )
					{
						overdue++;
						htmlR += '<img title="' + m.summary + '" src="/share/res/components/images/red-btn.png"  style="padding-right: 5px; width: 16px; height: 16px;" />';
					}
					else if( daysLeft < 7 )
					{
						warning++;
						htmlY += '<img title="' + m.summary + '" src="/share/res/components/images/yellow-btn.png"  style="padding-right: 5px; width: 16px; height: 16px;" />';
					}
					else
					{
						everythingOk++;
						htmlG += '<img title="' + m.summary + '" src="/share/res/components/images/green-btn.png"  style="padding-right: 5px; width: 16px; height: 16px;" />';
					}
				}
				elLiner.innerHTML = htmlR + htmlY + htmlG + htmlN;
			}
			var titleFieldFormatter = function(elLiner, oRecord, oColumn, oData)
			{
				var record = oRecord.getData();
				var url = Alfresco.constants.URL_PAGECONTEXT + "site/" + record.shortName + "/dashboard";
				elLiner.innerHTML = '<a href="' + url + '">' + record.title + '</a>';
			}

			var settings = {
				container: this.id + '-dataTable',
				source: YAHOO.lang.substitute(
					'{proxy}/api/alvex/projects/status',
					{
						proxy: Alfresco.constants.PROXY_URI
					}
				),

				columnSettings: [ 
					{
						key: 'title',
						label: this.msg('alvex.projects-summary.label.name'),
						sortable:true,
						resizeable:true,
						width: 300,
						formatter: titleFieldFormatter
					},
					{
						key: 'milestones',
						label: this.msg('alvex.projects-summary.label.milestones'),
						sortable:false,
						resizeable:true,
						width: 245,
						formatter: milestonesFieldFormatter
					},
					{
						key: 'workflows',
						label: this.msg('alvex.projects-summary.label.workflows'),
						sortable:true,
						resizeable:true,
						width: 250,
						formatter: workflowsFieldFormatter
					}
				],
				dataSourceSettings:{
					responseType:YdataSource.TYPE_JSON,
					responseSchema:
					{
						resultsList: 'data',
						fields:[
							{ key: 'title' },
							{ key: 'workflows' },
							{ key: 'milestones' },
							{ key: 'shortName' }
						]
					}
				}
			}

			new YdataTable(
	        	settings.container, 
				settings.columnSettings, 
				new YdataSource(
					settings.source, 
					settings.dataSourceSettings
				), 
				settings.dataTableSettings
			);

		}

	});
	
})();
