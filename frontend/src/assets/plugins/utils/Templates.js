
+function () {
    'use strict'

    var Templates = {
        handlerErrorTpl: function() {
            return multiline.stripIndent(function(){/*
                <dl class="dl-horizontal">
                    <dt>Code</dt>
                    <dd>{code}</dd>
                    <dt>Message</dt>
                    <dd>{message}</dd>
                    <dt>Data</dt>
                    <dd>{data}</dd>
                </dl>
           */});
        },
        handlerErrorWithoutDataTpl: function() {
            return multiline.stripIndent(function(){/*
                <dl class="dl-horizontal">
                    <dt>Code</dt>
                    <dd>{code}</dd>
                    <dt>Message</dt>
                    <dd>{message}</dd>
                </dl>
           */});
        },
        dataPopupTpl: function() {
            return multiline.stripIndent(function(){/*
               <div class="box box-solid">
                   <div class="box-header with-border">
                       <i class="fa fa-text-width"></i>
                       <h3 class="box-title">Data Information</b></h3>
                   </div>
                   <div class="box-body">

                       <div class="nav-tabs-custom">
                           <ul class="nav nav-tabs">
                               <li class="active"><a href="#datalayer_tab_basic" data-toggle="tab">Basic</a></li>
                               <li><a href="#datalayer_tab_values" data-toggle="tab">Values</a></li>
                           </ul>
                           <div class="tab-content">

                               <div class="tab-pane active" id="datalayer_tab_basic">
                                    <div class="box-body" style="min-width: 400px">
                                        <div class="box-body">
                                            <dl class="dl-horizontal">
                                                <dt>Farm</dt>
                                                <dd>{farm}</dd>
                                                <dt>Block</dt>
                                                <dd>{block}</dd>
                                                <dt>Tunnel</dt>
                                                <dd>{tunnel}</dd>
                                                <dt>Date</dt>
                                                <dd>{date}</dd>
                                                <dt>Longitude</dt>
                                                <dd>{longitude}</dd>
                                                <dt>Latitude</dt>
                                                <dd>{latitude}</dd>
                                            </dl>
                                        </div>
                                   </div>
                               </div>

                               <div class="tab-pane" id="datalayer_tab_values">
                                    <div class="box-body" style="min-width: 400px">
                                        <div class="box-body">
                                           <dl class="dl-horizontal">
                                               {props_values}
                                           </dl>
                                        </div>
                                   </div>
                               </div>

                           </div>
                       </div>

                   </div>
               </div>
           */});
        },
        rendererDataPopupTpl: function() {
            return multiline.stripIndent(function(){/*
               <div class="box box-solid">
                   <div class="box-header with-border">
                       {icon}
                       <h3 class="box-title"><b>{title}</b></h3>
                   </div>
                   <div class="box-body">

                       <div class="nav-tabs-custom">
                           <ul class="nav nav-tabs">
                               <li class="active"><a href="#datalayer_tab_basic" data-toggle="tab">Basic</a></li>
                               <li><a href="#datalayer_tab_values" data-toggle="tab">Values</a></li>
                           </ul>
                           <div class="tab-content">

                               <div class="tab-pane active" id="datalayer_tab_basic">
                                    <div class="box-body" style="min-width: 400px">
                                        <div class="box-body">
                                            <dl class="dl-horizontal">
                                                <dt>Farm</dt>
                                                <dd>{farm}</dd>
                                                <dt>Block</dt>
                                                <dd>{block}</dd>
                                                <dt>Tunnel</dt>
                                                <dd>{tunnel}</dd>
                                                <dt>Date</dt>
                                                <dd>{date}</dd>
                                                <dt>Longitude</dt>
                                                <dd>{longitude}</dd>
                                                <dt>Latitude</dt>
                                                <dd>{latitude}</dd>
                                            </dl>
                                        </div>
                                   </div>
                               </div>

                               <div class="tab-pane" id="datalayer_tab_values">
                                    <div class="box-body" style="min-width: 400px">
                                        <div class="box-body">
                                           <dl class="dl-horizontal">
                                               {props_values}
                                           </dl>
                                        </div>
                                   </div>
                               </div>

                           </div>
                       </div>

                   </div>
               </div>
           */});
        },
        bugWiki: function() {
            return multiline.stripIndent(function(){/*
                <table class="infobox biota" style="text-align: left; width: 200px; font-size: 100%">
                <tbody><tr>
                <th colspan="2" style="text-align: center; background-color: rgb(235,235,210)">Aphids<br>
                <small>Temporal range: <span style="display:inline-block;">Permian-present</span></small>
                <div id="Timeline-row" style="margin: 4px auto 0; clear:both; width:220px; padding:0px; height:18px; overflow:visible; border:1px #666; border-style:solid none; position:relative; z-index:0; font-size:13px;">
                <div style="position:absolute; height:100%; left:0px; width:208.07692307692px; padding-left:5px; text-align:left; background-color:rgb(254,214,123); background-image: -moz-linear-gradient(left, rgba(255,255,255,1), rgba(254,217,106,1) 15%, rgba(254,217,106,1)); background-image: -o-linear-gradient(left, rgba(255,255,255,1), rgba(254,217,106,1) 15%, rgba(254,217,106,1)); background-image: -webkit-linear-gradient(left, rgba(255,255,255,1), rgba(254,217,106,1) 15%, rgba(254,217,106,1)); background-image: linear-gradient(to right, rgba(255,255,255,1), rgba(254,217,106,1) 15%, rgba(254,217,106,1));"><small>Pre?</small></div>
                <div style="position:absolute; height:100%; text-align:center; background-color:rgb(129,170,114); left:36.892307692308px; width:18.818461538462px;"><small>?</small></div>
                <div style="position:absolute; height:100%; text-align:center; background-color:rgb(0,169,138); left:55.710769230769px; width:14.08px;"><small>O</small></div>
                <div style="position:absolute; height:100%; text-align:center; background-color:rgb(166,223,197); left:69.790769230769px; width:8.3261538461539px;"><small>S</small></div>
                <div style="position:absolute; height:100%; text-align:center; background-color:rgb(221,150,81); left:78.116923076923px; width:20.409230769231px;"><small>D</small></div>
                <div style="position:absolute; height:100%; text-align:center; background-color:rgb(63,174,173); left:98.526153846154px; width:20.307692307692px;"><small>C</small></div>
                <div style="position:absolute; height:100%; text-align:center; background-color:rgb(247,88,60); left:118.83384615385px; width:15.816307692308px;"><small>P</small></div>
                <div style="position:absolute; height:100%; text-align:center; background-color:rgb(153,78,150); left:134.65015384615px; width:17.217538461538px;"><small>T</small></div>
                <div style="position:absolute; height:100%; text-align:center; background-color:rgb(0,187,231); left:151.86769230769px; width:19.055384615385px;"><small>J</small></div>
                <div style="position:absolute; height:100%; text-align:center; background-color:rgb(111,200,107); left:170.92307692308px; width:26.738461538462px;"><small>K</small></div>
                <div style="position:absolute; height:100%; text-align:center; background-color:rgb(254,161,99); left:197.66153846154px; width:14.543692307692px;"><small><small>Pg</small></small></div>
                <div style="position:absolute; height:100%; text-align:center; background-color:rgb(254,221,45); left:212.20523076923px; width:6.9215384615385px;"><small>N</small></div>
                <div id="end-border" style="position:absolute; height:100%; background-color:#666; width:1px; left:219px"></div>
                <div style="margin:0 auto; line-height:0; clear:both; width:220px; padding:0px; height:8px; overflow:visible; background-color:transparent; position:relative; top:-4px; z-index:100;">
                <div style="position:absolute; height:8px; left:118.83384615385px; width:101.16615384615px; background-color:#360; opacity:0.42;"></div>
                <div style="position:absolute; height:8px; left:118.83384615385px; width:101.16615384615px; background-color:#360; opacity:1;"></div>
                <div style="position:absolute; height:6px; top:1px; left:119.83384615385px; width:99.166153846154px; background-color:#6c3;"></div>
                </div>
                </div>
                </th>
                </tr>
                <tr>
                <td colspan="2" style="text-align: center"><img alt="Aphid on leaf05.jpg" src="images/aphid.jpg" width="220" height="157" srcset="images/aphid.jpg" data-file-width="5826" data-file-height="4164"></td>
                </tr>
                <tr>
                <th colspan="2" style="text-align: center; background-color: rgb(235,235,210)">Scientific classification</th>
                </tr>
                <tr>
                <td>Kingdom:</td>
                <td><span class="kingdom"><a href="#">Animalia</a></span></td>
                </tr>
                <tr>
                <td>Phylum:</td>
                <td><span class="phylum"><a href="#">Arthropoda</a></span></td>
                </tr>
                <tr>
                <td>Class:</td>
                <td><span class="class"><a href="#">Insecta</a></span></td>
                </tr>
                <tr>
                <td>Order:</td>
                <td><span class="order"><a href="#">Hemiptera</a></span></td>
                </tr>
                <tr>
                <td>Suborder:</td>
                <td><span class="suborder"><a href="#">Sternorrhyncha</a></span></td>
                </tr>
                <tr>
                <td>Superfamily:</td>
                <td><span class="superfamily"><b>Aphidoidea</b></span><br>
                <small><a href="#">Geoffroy, 1762</a></small></td>
                </tr>
                </tbody></table>
            */});
        },
        bugMouseOverTpl: function() {
            return multiline.stripIndent(function(){/*
               <div class="box box-solid">
                   <div class="box-header with-border">
                       <i class="fa fa-text-width"></i>
                       <h3 class="box-title">Bug Information</h3>
                   </div>

                   <div class="box-body">
                        {bugWiki}
                   </div>
               </div>
           */});
        },
        bugDetailPopupTpl: function() {
            return multiline.stripIndent(function(){/*
                <div class="box box-solid">
                    <div class="box-header with-border">
                        <i class="fa fa-text-width"></i>
                        <h3 class="box-title">Bug Detail</h3>
                    </div>

                    <div class="box-body">

                        <div class="nav-tabs-custom">
                            <ul class="nav nav-tabs">
                                <li class="active"><a href="#bug_detail_tab_info" data-toggle="tab">Information</a></li>
                                <li><a href="#bug_wiki_tab_info" data-toggle="tab">Wiki</a></li>
                            </ul>
                            <div class="tab-content">
                                <div class="tab-pane active" id="bug_detail_tab_info" style="min-width: 480px">

                                    <div class="box-group" id="bugdetailGroup">
                                        <!-- we are adding the .panel class so bootstrap.js collapse plugin detects it -->
                                        <div class="panel box box-success" style="min-width: 400px">
                                            <div class="box-header with-border">
                                                <h4 class="box-title">
                                                <a data-toggle="collapse" data-parent="#bugdetailGroup" href="#bugBasicDetail">
                                                    Bug Information
                                                </a>
                                                </h4>
                                            </div>
                                            <div id="bugBasicDetail" class="panel-collapse collapse in">
                                                <div class="box-body">
                                                    <dl class="dl-horizontal">
                                                        <dt>Bug Type</dt>
                                                        <dd>Aphids</dd>
                                                        <dt>Bug Coordinates (Lng, Lat)</dt>
                                                        <dd>{place}</dd>
                                                    </dl>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="panel box box-success">
                                            <div class="box-header with-border">
                                                <h4 class="box-title">
                                                <a data-toggle="collapse" data-parent="#bugdetailGroup" href="#bugBlockDetail">
                                                    Place Information
                                                </a>
                                                </h4>
                                            </div>
                                            <div id="bugBlockDetail" class="panel-collapse collapse">
                                                <div class="box-body">
                                                    <dl class="dl-horizontal">
                                                        {props}
                                                    </dl>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="tab-pane" id="bug_wiki_tab_info">
                                    <div class="box-body">
                                        {bugWiki}
                                    </div>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>
            */});
        },
        polygonCalendarItemTpl: function() {
            return multiline.stripIndent(function(){/*
               <div class="panel box box-success" style="min-width: 40em; max-width: 50%;">
                   <div class="box-header with-border">
                       <h4 class="box-title">
                       <a data-toggle="collapse" data-parent="#calendarGroup" href="#{id}">
                           {title}
                       </a>
                       </h4>
                   </div>
                   <div id="{id}" class="panel-collapse collapse {inStyle}">
                       <div class="box-body">
                           <dl class="dl-horizontal">
                                <dt>Event</dt>
                                <dd>{event}</dd>
                                <dt>Start</dt>
                                <dd>{start}</dd>
                                <dt>End</dt>
                                <dd>{end}</dd>
                                <dt>Block</dt>
                                <dd>{block}</dd>
                                <dt>Tunnel</dt>
                                <dd>{tunnel}</dd>
                                <dt>Priority</dt>
                                <dd>{priority}</dd>
                                <dt>Note</dt>
                                <dd>{note}</dd>
                           </dl>
                       </div>
                   </div>
               </div>
           */});
        },
        polygonMouseOverTpl: function() {
            return multiline.stripIndent(function(){/*
               <div class="box box-solid">
                   <div class="box-header with-border">
                       <i class="fa fa-text-width"></i>
                       <h3 class="box-title">Information of <b>{name}</b></h3>
                   </div>

                   <div class="box-body">
                       <dl class="dl-horizontal">
                           {props}
                       </dl>
                   </div>
               </div>
           */});
        },
        polygonPopupTpl: function() {
            return multiline.stripIndent(function(){/*
               <div id="polygonPopupWindow" class="box box-solid">
                   <div class="box-header with-border">
                       <i class="fa fa-text-width"></i>
                       <h3 class="box-title">Detail of <b>{name}</b></h3>
                   </div>
                   <div class="box-body">

                       <div class="nav-tabs-custom">
                           <ul class="nav nav-tabs">
                               <li class="active"><a href="#tab_info" data-toggle="tab">Information</a></li>
                               <li><a href="#tab_chart" data-initialized="false" data-toggle="tab" onclick="window.charts.rendererPolygonCharts(this);">Chart</a></li>
                               <li id="li_tab_calendar" style="display:none;"><a id="a_tab_calendar" href="#tab_calander" data-toggle="tab" onclick="window.rendererPolygonCalendars(this);">Calendar</a></li>
                           </ul>
                           <div class="tab-content">
                               <div class="tab-pane active" id="tab_info">
                                   <div class="box-group" id="informationGroup">
                                       <!-- we are adding the .panel class so bootstrap.js collapse plugin detects it -->
                                       <div class="panel box box-success" style="min-width: 40em; max-width: 50%;">
                                           <div class="box-header with-border">
                                               <h4 class="box-title">
                                               <a data-toggle="collapse" data-parent="#informationGroup" href="#basicInformation">
                                                   Basic
                                               </a>
                                               </h4>
                                           </div>
                                           <div id="basicInformation" class="panel-collapse collapse in">
                                               <div class="box-body">
                                                   <dl class="dl-horizontal">
                                                       {props}
                                                   </dl>
                                               </div>
                                           </div>
                                       </div>

                                       <div class="panel box box-success">
                                           <div class="box-header with-border">
                                               <h4 class="box-title">
                                               <a data-toggle="collapse" data-parent="#informationGroup" href="#detailInformation">
                                                   Detail
                                               </a>
                                               </h4>
                                           </div>
                                           <div id="detailInformation" class="panel-collapse collapse">
                                               <div class="box-body">
                                                   <dl class="dl-horizontal">
                                                       <dt>Perimeter (meters)</dt>
                                                       <dd>{perimeter}</dd>
                                                       <dt>Area (square meters)</dt>
                                                       <dd>{area}</dd>
                                                       <dt>Coordinates (Lng, Lat)</dt>
                                                       {coordinates}
                                                   </dl>
                                               </div>
                                           </div>
                                       </div>
                                   </div>
                               </div>

                                <div id="tab_chart" class="tab-pane carousel-charts carousel slide" data-ride="carousel" data-interval="false">
                                    <ol class="carousel-indicators">
                                    </ol>
                                    <div class="carousel-inner">
                                    </div>
                                    <a class="left carousel-control" href="#tab_chart" data-slide="prev">
                                        <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
                                        <span class="sr-only">Previous</span>
                                    </a>
                                    <a class="right carousel-control" href="#tab_chart" data-slide="next">
                                        <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
                                        <span class="sr-only">Next</span>
                                    </a>
                                </div>

                                <div id="tab_calander" class="tab-pane carousel-charts carousel slide" data-ride="carousel" data-interval="false">
                                    Calendar
                                </div>

                           </div>
                       </div>

                   </div>
               </div>
           */});
        }
    }

    window.Templates = Templates;
}()