
+function ($) {
    'use strict'

    L.Control.GeoJSONSelector = L.Control.extend({
        //Managed Events:
        //	change				{layers}               fired after checked item in list
        //

        options: {
            collapsed: false,				//collapse panel list
            position: 'bottomleft',			//position of panel list
            extClass: null,

            layerFarmName: null,		    //the layer control farm name

            defaultPanelName: 'Farm Panel',	//the layer control group name
            layerPanelName: null,		    //the layer control group name

            listLabelPre: null,	            //insert before every label
            listLabel: 'properties.name',	//GeoJSON property to generate items list
            listLabelNullValue: 'N/A',
            listSortBy: null,				//GeoJSON property to sort items list, default listLabel
            listItemBuild: null,			//function list item builder

            enablePopup: true,
            activeListFromLayer: true,		//enable activation of list item from layer
            zoomToLayer: false,

            listOnlyVisibleLayers: false,	//show list of item of layers visible in map canvas

            initializedHook: null,          //after initialized invoke some code
            activeLayerAtFirst: true,       //display the geojson at first or not

            enableCalendarLayers: false,       //display the calendar control and refresh calendar layers
            activeCalendarLabelAtFirst: true,//display the calendar title at first or not
            activeCalendarIconAtFirst: true,//display the calendar icon at first or not

            farmMapId: null,                    //id of FarmMaps collection

            blockPropName: '_BLOCK_',	        //customized block properties name
            tunnelPropName: '_TUNNEL_',	        //customized tunnel properties name

            multiple: false,				//active multiple selection

            style: {
                color:'#fff',
                fillColor:'#2b8cbe',
                fillOpacity: 0.4,
                opacity: 1,
                weight: 1
            },
            activeClass: 'active',			//css class name for active list items
            activeStyle: {					//style for Active GeoJSON feature
                color:'#fff',
                fillColor:'brown',
                fillOpacity: 0.5,
                opacity: 1,
                weight: 3
            },
            selectClass: 'selected',
            selectStyle: {
                color:'#fff',
                fillColor:'brown',
                fillOpacity: 0.7,
                opacity: 1,
                weight: 2
            }
        },

        initialize: function(layer, options) {
            L.Util.setOptions(this, options || {});
            var that = this;

            this.options.listSortBy = this.options.listSortBy || this.options.listLabel;

            if(this.options.listItemBuild)
                this._itemBuild = this.options.listItemBuild;

            this._layer = layer;

            //XXX if in demo profile, change the opacity to lower
            if(window.profile == 'demo'){
                this.options.style.fillOpacity = 1;
                this.options.activeStyle.fillOpacity = 1;
                this.options.selectStyle.fillOpacity = 1;
            }
        },

        onAdd: function (map) {
            var that = this;
            var className = 'leftPaneClosed geojson-list bottomLeftCtr';
            if(this.options.extClass)
                className += ' ' + this.options.extClass;
            var container = L.DomUtil.create('div', className);
            this._baseName = 'geojson-list';
            this._map = map;
            this._container = container;
            this._id = this._baseName + L.stamp(this._container);
            this._list = L.DomUtil.create('ul', 'geojson-list-group', container);
            this._items = [];

            this._initGeoJSON();

            this._calendarLabelLayers = L.layerGroup();
            this._calendarIconLayers = L.layerGroup();
            this._blinkMarkers = [];
            // This is where the magic happens!
            this._textIconLayers = L.featureGroup.subGroup();

            L.DomEvent
                .on(container, 'mouseover', function () {
                    map.scrollWheelZoom.disable();
                })
                .on(container, 'mouseout', function () {
                    map.scrollWheelZoom.enable();
                });

            if(this.options.listOnlyVisibleLayers)
                map.on('moveend', this._updateListVisible, this);

            map.whenReady(function() {
                container.style.maxHeight = (map.getSize().y-110)+'px';
            });

            //update the list when the layers control is clicked
            $(document).on('click', 'input.leaflet-control-layers-selector', function () {
                that._updateListVisible();
            });

            this._initToggle();
            this._updateList();
            if(this.options.enableCalendarLayers)
                this._initLayersControl();

            if(this.options.initializedHook){
                this.options.initializedHook.call(this, this._layer, this.options.layerFarmName);
            }

            //if remove the geojson, hide the left control list as well.
            this._layer.on('add', function (a) {
                that._displayLeftListBtn();
                that._updateListVisible();
                if(that.options.zoomToLayer)
                    that._moveTo(that._layer);

            }).on('remove', function (a) {
                that._hideLeftListBtn();
            });

            //remove the geojson if set not activated, must add it first because the renderer need the layer information
            if(!this.options.activeLayerAtFirst){
                this._map.removeLayer(this._layer);
            }

            return container;
        },

        onRemove: function(map) {
            //remove calendar layers and map controls
            this._calendarLabelLayers.clearLayers();
            this._calendarIconLayers.clearLayers();
            for (var i = 0; i < this._blinkMarkers.length; i++)
                this._map.removeLayer(this._blinkMarkers[i]);

            this._removeLayersControl();

            //disassociate the map object
            this._map.off('moveend', this._updateList, this);
            this._map.off('moveend', this._updateListVisible, this);
            L.DomEvent.off(this._container, 'mouseover', L.DomEvent.stop);
            L.DomEvent.off(this._container, 'mouseout', L.DomEvent.stop);
            L.DomEvent.off(this._container, 'moveend', L.DomEvent.stop);
            this._map = null;
            return this;
        },

        reload: function(geoLayer) {
            var that = this;
            this._layer.eachLayer(function(layer) {
                layer.off();
            });
            this._layer = geoLayer;
            this._updateList();
        },

        addGeoJsonLayer: function(geoJsonLayer) { //should has feature attribute
            var that = this;
            this._layer.eachLayer(function(layer) {
                layer.off();
            });
            this._layer.addLayer(geoJsonLayer);
            this._updateList();
        },

        removeGeoJsonLayer: function(geoJsonLayer) { //should has feature attribute
            var that = this;
            this._layer.eachLayer(function(layer) {
                layer.off();
            });
            this._layer.removeLayer(geoJsonLayer);
            this._updateList();
        },

        _getLastFromPath: function(obj, prop) {
            var parts = prop.split('.'),
                last = parts.pop();
            return last;
        },

        _getPath: function(obj, prop) {
            var parts = prop.split('.'),
                last = parts.pop(),
                len = parts.length,
                cur = parts[0],
                i = 1;

            if(len > 0)
                while((obj = obj[cur]) && i < len)
                    cur = parts[i++];

            if(obj)
                return obj[last];
        },

        _itemBuild: function(layer) {

            return (this.options.listLabelPre || '') + (this._getPath(layer.feature, this.options.listLabel) || this.options.listLabelNullValue);
        },

        _selectItem: function(item, selected) {

            for (var i = 0; i < this._items.length; i++)
                L.DomUtil.removeClass(this._items[i], this.options.selectClass);

            if(selected)
                L.DomUtil.addClass(item, this.options.selectClass );
        },

        _selectLayer: function(layer, selected) {
            for(var i = 0; i < this._items.length; i++){
                if(this._items[i].layer.setStyle){
                    //update style for calendar or default
                    this._setStyleForLayer(this._items[i].layer);
                }
            }

            if(selected && layer.setStyle)
                layer.setStyle( this.options.selectStyle );
        },

        //initiate checkbox input group and it's objects and events
        _createItem: function(layer) {

            var that = this,
                item = L.DomUtil.create('li','geojson-list-item'),
                label = document.createElement('label'),
                inputType = this.options.multiple ? 'checkbox' : 'radio',
                input = this._createInputElement(inputType, this._id, false),
                html = this._itemBuild.call(this, layer);

            label.innerHTML = html;
            label.insertBefore(input, label.firstChild);
            item.appendChild(label);

            item.layer = layer;
            layer.itemList = item;
            layer.itemLabel = label;
            layer.inputObj = input;

            L.DomEvent
                //.disableClickPropagation(item)
                .on(item, 'click', L.DomEvent.stop, this)
                .on(item, 'click', function() {
                    if(that.options.zoomToLayer)
                        that._moveTo( layer );
                    //TODO zoom to bbox for multiple layers

                    input.checked = !input.checked;

                    item.selected = input.checked;

                    that._selectItem(item, input.checked);
                    that._selectLayer(layer, input.checked);

                    that.fire('change', {
                        selected: input.checked,
                        layers: [layer]
                    });

                    if(input.checked){
                        layer.openPopup();
                    }
                    else{
                        layer.closePopup();
                    }
                    //layer.fire("click");
                }, this)
                .on(item, 'mouseover', function(e) {
                    L.DomUtil.addClass(e.target, this.options.activeClass);
                    if(layer.setStyle)
                        layer.setStyle( that.options.activeStyle );
                }, this)
                .on(item, 'mouseout', function(e) {
                    L.DomUtil.removeClass(e.target, that.options.activeClass);
                    if(layer.inputObj.checked && layer.setStyle)
                        layer.setStyle( that.options.selectStyle );
                    else if(layer.setStyle)
                        //update style for calendar or default
                        that._setStyleForLayer(layer);
                }, this);

            this._items.push( item );

            return item;
        },

        // IE7 bugs out if you create a radio dynamically, so you have to do it this hacky way (see http://bit.ly/PqYLBe)
        _createInputElement: function (type, name, checked) {

            var radioHtml = '<input class="radio" type="'+type+'" name="' + name + '"';
            if (checked)
                radioHtml += ' checked="checked"';
            radioHtml += '/>';

            var radioFragment = document.createElement('div');
            radioFragment.innerHTML = radioHtml;

            return radioFragment.firstChild;
        },

        //now it has performance issue
        _selectBlockTunnel: function(block, tunnel) {
            var that = this;
            this._layer.eachLayer(function(layer) {
                if (layer.feature.properties) {
                    var blockVal = that.getBlockVal(layer);
                    var tunnelVal = that.getTunnelVal(layer);
                    if(block == blockVal && tunnel == tunnelVal){
                        layer.itemLabel.click();
                    }
                }
            });
        },

        getLayerByBlockTunnel: function(block, tunnel) {
            var that = this;
            var _layers = this._layer.getLayers();
            for (var i = 0; i < _layers.length; i++){
                var layer = _layers[i];
                if (layer.feature.properties) {
                    var blockVal = that.getBlockVal(layer);
                    var tunnelVal = that.getTunnelVal(layer);
                    if(block == blockVal && tunnel == tunnelVal){
                        return layer;
                    }
                }
            }
            return null;
        },

        getBlockVal: function(layer) {
            var blockVal = layer.feature.properties[this.options.blockPropName];
            return blockVal
        },

        getTunnelVal: function(layer) {
            var tunnelVal = layer.feature.properties[this.options.tunnelPropName];
            return tunnelVal
        },

        _updateList: function() {
            var that = this,
                layers = [],
                sortProp = this.options.listSortBy;

            this._list.innerHTML = '';
            this._layer.eachLayer(function(layer) {
                layers.push( layer );

                var layerName = that._getPath(layer.feature, that.options.listLabel) || that.options.listLabelNullValue;
                if (layer.feature.properties && layerName) {
                    //bind mouse over and popup contents to the layer
                    if(that.options.enablePopup)
                        that._bindPopupForLayer(layer, layerName);
                }

                if(that.options.activeListFromLayer) {
                    layer
                    .on('click', L.DomEvent.stop)
                    .on('click', function() {
                        layer.itemLabel.click();
                    })
                    .on('mouseover', function() {
                        if(layer.setStyle)
                            layer.setStyle( that.options.activeStyle );
                        L.DomUtil.addClass(layer.itemList, that.options.activeClass);
                    })
                    .on('mouseout', function() {
                        if(layer.inputObj.checked && layer.setStyle)
                            layer.setStyle( that.options.selectStyle );
                        else if(layer.setStyle)
                            //update style for calendar or default
                            that._setStyleForLayer(layer);
                        L.DomUtil.removeClass(layer.itemList, that.options.activeClass);
                    });
                }

                //update style for calendar or default
                that._setStyleForLayer(layer);

            });

            layers.sort(function(a, b) {
                var ap = that._getPath(a.feature, sortProp),
                    bp = that._getPath(b.feature, sortProp);
                if(!ap || !bp)
                    return 0;
                if(ap.length < bp.length)
                    return -1;
                else if(ap.length > bp.length)
                    return 1;
                if(ap < bp)
                    return -1;
                else if(ap > bp)
                    return 1;
                return 0;
            });

            for (var i=0; i<layers.length; i++)
                this._list.appendChild( this._createItem( layers[i] ) );

            if(this.options.enableCalendarLayers)
                this._updateCalendarLayer();
        },

        _updateCalendarLayer: function(){
            var that = this;
            var json = {
                datasetId: GlobalUtils.datasetId,
                farmMapId: this.options.farmMapId
            };
            $.sync({
                type: 'POST',
                url: '/data/currentCalendarLayerByFarmMap',
                data: json,
                success : function(data) {
                    if(data){
                        //loop each calendar layer data and add it
                        $.each(data, function(i, eachData) {
                            var block = eachData.block;
                            var tunnel = eachData.tunnel;
                            var _layer = that.getLayerByBlockTunnel(block, tunnel);
                            if(_layer) {
                                _layer.calendarStyle = {
                                    color: eachData.color,
                                    fillColor: eachData.color,
                                    fillOpacity: 0.4,
                                    opacity: 1,
                                    weight: 1
                                };
                                _layer.setStyle(_layer.calendarStyle);
                                _layer.calendarEvent = {
                                    event: eachData.event
                                };

                                //add label to the label layer group
                                var eventLabel = L.DomUtil.create('b', 'geojson-text-label leaflet-zoom-hide');
                                eventLabel.innerHTML = eachData.event;
                                var calendarLabelLayer = new L.DomLayer(that._map, _layer, eventLabel, {detectingNumToDisplay: 0, redundant: 5, position: 'center', offsetX: 0, offsetY: 10});
                                that._calendarLabelLayers.addLayer(calendarLabelLayer);
                                _layer.calendarLabelLayer = calendarLabelLayer;

                                //add icon to the icon layer group
                                var calendarIcon;
                                if(eachData.icon)
                                    calendarIcon = $(eachData.icon);
                                else
                                    calendarIcon = $('<i class="fa fa-exclamation"></i>');
                                calendarIcon.addClass("info-icon");
                                calendarIcon.css('color', eachData.iconColor);
                                calendarIcon.css('display','inline');
                                calendarIcon.css('font-size','20px');
                                calendarIcon.css('width','20px');
                                calendarIcon.css('height','20px');
                                var calendarIconLayer = new L.DomLayer(that._map, _layer, calendarIcon[0], {detectingNumToDisplay: 0, position: 'center', offsetX: 0, offsetY: -10});
                                that._calendarIconLayers.addLayer(calendarIconLayer);
                                _layer.calendarIconLayer = calendarIconLayer;

                                var title;
                                if(tunnel)
                                    title = "Calendar: <b>"+eachData.event+"</b> (Block: <b>" + block + "</b>, Tunnel: <b>" + tunnel + "</b>)";
                                else
                                    title = "Calendar: <b>"+eachData.event+"</b> (Block: <b>" + block + "</b>)";

                                $(eventLabel).attr('data-toggle', 'tooltip');
                                $(eventLabel).attr('data-placement', 'bottom');
                                $(eventLabel).attr('data-html', 'true');
                                $(eventLabel).attr('data-title', title);
                                $(eventLabel).tooltip();

                                calendarIcon.attr('data-toggle', 'tooltip');
                                calendarIcon.attr('data-placement', 'bottom');
                                calendarIcon.attr('data-html', 'true');
                                calendarIcon.attr('data-title', title);
                                calendarIcon.tooltip();

                                if(eachData.blink && eachData.blink!==''){
                                    var center = that._getCenter(_layer);
                                    var marker = L.marker(center, {highlight: 'blink ' + eachData.blink, opacity: 0});
                                    that._blinkMarkers.push(marker);
                                    marker.addTo(that._map);

                                    $(marker._icon).attr('data-toggle', 'tooltip');
                                    $(marker._icon).attr('data-placement', 'bottom');
                                    $(marker._icon).attr('data-html', 'true');
                                    $(marker._icon).attr('data-title', title);
                                    $(marker._icon).tooltip();
                                }
                            }
                        });
                    }
                }
            });
        },

        _setStyleForLayer: function(layer) {
            if(layer.calendarStyle && layer.setStyle)
                layer.setStyle(layer.calendarStyle);
            else
                layer.setStyle(this.options.style);
        },

        _bindPopupForLayer: function(layer, layerName) {
            var that = this;
            var props = '';
            var name = null;
            var block = null;
            var tunnel = null;
            for (var p in layer.feature.properties) {
                var nameProp = that._getLastFromPath(layer.feature, that.options.listLabel);
                if(p == nameProp){
                    name = layer.feature.properties[p];
                    props += '<dt style="line-height: 1em;">' + p + ' <span class="label label-primary" style="vertical-align: middle;">Name</span></dt>';
                }
                else if(p == that.options.blockPropName){
                    block = layer.feature.properties[p];
                    props += '<dt style="line-height: 1em;">' + p + ' <span class="label label-primary" style="vertical-align: middle;">Block</span></dt>';
                }
                else if(p == that.options.tunnelPropName){
                    tunnel = layer.feature.properties[p];
                    props += '<dt style="line-height: 1em;">' + p + ' <span class="label label-primary" style="vertical-align: middle;">Tunnel</span></dt>';
                }
                else{
                    props += '<dt>' + p + '</dt>';
                }
                props += '<dd>' + layer.feature.properties[p] + '</dd>';
            }
            var perimeter = Utils.calcPathsDistance(layer.feature.geometry.coordinates[0]).toFixed(2);
            var area = Utils.calcArea(layer.feature.geometry.coordinates[0]).toFixed(2);
            var coordinates = "";
            for (var i = 0, l = layer.feature.geometry.coordinates[0].length; i < l; i += 1) {
                coordinates += '<dd>' + layer.feature.geometry.coordinates[0][i] + '</dd>';
            }

            //bind mouse over content to the layer
            var mouseoverContent = L.Util.template(Templates.polygonMouseOverTpl(), {
                name: layerName,
                props: props
            });
            var title = name?("Name:<b style='color:red;'>"+name+"</b>"):"";
            if(block && !tunnel){
                title += " (Block:<b style='color:red;'>" + block + "</b>)";
            }
            else if(!block && tunnel){
                title += " (Tunnel:<b style='color:red;'>" + tunnel + "</b>)";
            }
            else if(block && tunnel){
                title += " (Block:<b style='color:red;'>" + block + "</b>, Tunnel:<b style='color:red;'>" + tunnel + "</b>)";
            }
            layer.bindTooltip(title);

            //bind popup content to the layer
            var popupContent = L.Util.template(Templates.polygonPopupTpl(), {
                name: layerName,
                props: props,
                perimeter: perimeter,
                area: area,
                coordinates: coordinates
            });
            var customOptions = {
                'maxWidth': 'auto',
                'autoPan': false,
                'keepInView': false,
                'autoPanPaddingTopLeft': L.point(30, 100)
            };

            //whenever popup open, dynamically display the calendar if it has
            var popup = new L.Popup(customOptions, layer);
            popup.on('contentupdate', function(e){
                $('#polygonPopupWindow #a_tab_calendar').data('farmId', GlobalUtils.farmId);
                $('#polygonPopupWindow #a_tab_calendar').data('block', block);
                $('#polygonPopupWindow #a_tab_calendar').data('tunnel', tunnel);
                var json = {
                    farmId: GlobalUtils.farmId,
                    block: block,
                    tunnel: tunnel
                };
                $.sync({
                    type: 'POST',
                    data: json,
                    url: '/data/currentCalendarStatusByArea',
                    success: function(data) {
                        if(data){
                            $('#polygonPopupWindow #li_tab_calendar').css('display', 'block');
                        }
                    }
                });
            });
            popup.setContent(popupContent);

            layer.bindPopup(popup);
            layer.on('popupclose', function(e){
                $('body').trigger($.Event(GlobalUtils.Event.popupclose));
            });
        },

        _getCenter: function(layer){
            var _posLatLng;
            if(this._map.hasLayer(layer)){
                _posLatLng = layer.getCenter();
            }
            else{
                this._map.addLayer(layer);
                _posLatLng = layer.getCenter();
                this._map.removeLayer(layer);
            }
            return _posLatLng;
        },

        _updateListVisible: function() {
            var that = this,
                visible;
            var visibleNum = 0;
            this._layer.eachLayer(function(layer) {
                if(layer.getBounds)
                    visible = that._map.getBounds().intersects( layer.getBounds() );
                else if(layer.getLatLng)
                    visible = that._map.getBounds().contains( layer.getLatLng() );

                //if the layer is hidden by layers control
                if(visible && !that._map.hasLayer(layer)){
                    visible = false;
                }
                if(visible)
                    visibleNum ++;
                layer.itemList.style.display = visible ? 'block':'none';
            });
            if(visibleNum === 0){
                that._hideLeftListBtn();
            }
            else{
                that._displayLeftListBtn();
            }
        },

        _initLayersControl: function () {
            var layerPanelName = this.options.layerPanelName? this.options.layerPanelName: this.options.defaultPanelName;
            var farmOverLayers = [
                {
                    group: layerPanelName,
                    collapsed: true,
                    layers: [
                        {
                            name: "Map",
                            icon: '<i class="fa fa-map-o"></i>',
                            layer: this._layer,
                            active: true
                        },
                        {
                            name: "Calendar Event",
                            icon: '<i class="fa fa-square-o"></i>',
                            layer: this._calendarLabelLayers,
                            active: this.options.activeCalendarLabelAtFirst
                        },
                        {
                            name: "Calendar Icon",
                            icon: '<i class="fa fa-info"></i>',
                            layer: this._calendarIconLayers,
                            active: this.options.activeCalendarIconAtFirst
                        }
                    ]
                }
            ];
            this.farmPanelLayers = L.control.panelLayers(null, farmOverLayers, {compact:true, collapsibleGroups: true, position: 'topleft', className: 'leftPaneClosed bottomLeftCtr mapLayerCtrl'});
            this._map.addControl(this.farmPanelLayers);
        },

        _removeLayersControl: function() {
            this._map.removeControl(this.farmPanelLayers);
        },

        _initGeoJSON: function() {
            this._layer.addTo(this._map);
            if(this._layer.getBounds() && this._layer.getBounds().isValid())
                this._map.fitBounds(this._layer.getBounds());
        },

        _initToggle: function () {

            /* inspired by L.Control.Layers */

            var container = this._container;

            //Makes this work on IE10 Touch devices by stopping it from firing a mouseout event when the touch is released
            container.setAttribute('aria-haspopup', true);

            if (!L.Browser.touch) {
                L.DomEvent
                    .disableClickPropagation(container);
                    //.disableScrollPropagation(container);
            } else {
                L.DomEvent.on(container, 'click', L.DomEvent.stopPropagation);
            }

            if (this.options.collapsed)
            {
                this._collapse();

                if (!L.Browser.android) {
                    L.DomEvent
                        .on(container, 'mouseover', this._expand, this)
                        .on(container, 'mouseout', this._collapse, this);
                }
                var link = this._button = L.DomUtil.create('a', 'geojson-list-toggle', container);
                link.href = '#';
                link.title = 'List GeoJSON';

                if (L.Browser.touch) {
                    L.DomEvent
                        .on(link, 'click', L.DomEvent.stop)
                        .on(link, 'click', this._expand, this);
                }
                else {
                    L.DomEvent.on(link, 'focus', this._expand, this);
                }

                this._map.on('click', this._collapse, this);
                // TODO keyboard accessibility
            }
        },

        _expand: function () {
            this._container.className = this._container.className.replace(' geojson-list-collapsed', '');
        },

        _collapse: function () {
            L.DomUtil.addClass(this._container, 'geojson-list-collapsed');
        },

        _hideLeftListBtn: function () {
            L.DomUtil.addClass(this._container, 'hidden');
        },

        _displayLeftListBtn: function () {
            L.DomUtil.removeClass(this._container, 'hidden');
        },

        _moveTo: function(layer) {

            var pos = this.options.position;

            var psize = new L.Point(
                    this._container.clientWidth,
                    this._container.clientHeight),
                fitOpts = {
                    paddingTopLeft: null,
                    paddingBottomRight: null
                };

            fitOpts.paddingTopLeft = L.point(30, 100);
            fitOpts.maxZoom = 18;

            if(layer.getBounds() && layer.getBounds().isValid())
                this._map.fitBounds(layer.getBounds(), fitOpts);

            else if(layer.getLatLng)
                this._map.setView( layer.getLatLng() );

        }
    });

    //To support the on/off events
    L.Control.GeoJSONSelector.include(L.Evented.prototype);

    L.control.geoJsonSelector = function (layer, options) {
        return new L.Control.GeoJSONSelector(layer, options);
    };


}(jQuery)
