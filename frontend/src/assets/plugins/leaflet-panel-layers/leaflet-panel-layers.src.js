/* 
 * Leaflet Panel Layers v0.9.5 - 2016-12-08 
 * 
 * Copyright 2016 Stefano Cudini 
 * stefano.cudini@gmail.com 
 * http://labs.easyblog.it/ 
 * 
 * Licensed under the MIT license. 
 * 
 * Demos: 
 * http://labs.easyblog.it/maps/leaflet-panel-layers/ 
 * 
 * Source: 
 * git@github.com:stefanocudini/leaflet-panel-layers.git 
 * 
 */
(function (factory) {
    if(typeof define === 'function' && define.amd) {
    //AMD
        define(['leaflet'], factory);
    } else if(typeof module !== 'undefined') {
    // Node/CommonJS
        module.exports = factory(require('leaflet'));
    } else {
    // Browser globals
        if(typeof window.L === 'undefined')
            throw 'Leaflet must be loaded first';
        factory(window.L);
    }
})(function (L) {

L.Control.PanelLayers = L.Control.Layers.extend({

	//
	//Events:
	//	Event				Data passed		Description
	//
	//	panel:selected		{layerDef}		fired when an item of panel is added
	//	panel:unselected	{layerDef}		fired when an item of panel is removed
	//
	//Methods:
	//	Method 			Data passed		Description
	//
	//	addBaseLayer	{panel item}	add new layer item defition to panel as baselayers
	//	addOverlay		{panel item}	add new layer item defition to panel as overlay
	//	removeLayer	    {panel item}	remove layer item from panel
	//
	//Static Methods:
	//	Method 					Data passed		Description
	//
	//	configToControlLayers	{layerDef}		convert config from Control.PanelLayers to Control.Layers
	//

	options: {
		compact: false,
		collapsed: false,
		autoZIndex: true,
		collapsibleGroups: false,
		buildItem: null,				//function that return row item html node(or html string)
		title: '',						//title of panel
		//button: false, //TODO supporto button mode
		className: '',					//additional class name for panel
		position: 'topright'
	},

	initialize: function (baseLayers, overlays, options) {
		L.setOptions(this, options);
		this._layers = [];
		this._groups = {};
		this._items = {};
		this._layersActives = [];
		this._lastZIndex = 0;
		this._handlingClick = false;

		this.className = 'leaflet-panel-layers';


		var i, n, isCollapsed;

		for (i in baseLayers) {
			if(baseLayers[i].group && baseLayers[i].layers) {
				isCollapsed = baseLayers[i].collapsed || false;
				for(n in baseLayers[i].layers)
					this._addLayer(baseLayers[i].layers[n], false, baseLayers[i].group, isCollapsed);
			}
			else
				this._addLayer(baseLayers[i], false);
		}

		for (i in overlays) {
			if(overlays[i].group && overlays[i].layers) {
				isCollapsed = overlays[i].collapsed || false;
				for(n in overlays[i].layers)
					this._addLayer(overlays[i].layers[n], true, overlays[i].group, isCollapsed);
			}
			else
				this._addLayer(overlays[i], true);
		}
	},

	onAdd: function (map) {

		var self = this;

		for(var i in this._layersActives) {
			map.addLayer(this._layersActives[i]);
		}

		L.Control.Layers.prototype.onAdd.call(this, map);

		this._map.on('resize', function(e) {
            //this._form.style.maxHeight = (e.newSize.y-30)+'px';
            self._updateHeight(e.newSize.y);
        });

		return this._container;
	},

	//TODO addBaseLayerGroup
	//TODO addOverlayGroup

	addBaseLayer: function (layer, name, group) {
		layer.name = name || layer.name || '';
		this._addLayer(layer, false, group);
		this._update();
		return this;
	},

	addOverlay: function (layer, name, group) {
		layer.name = name || layer.name || '';
		this._addLayer(layer, true, group);
		this._update();
		return this;
	},

	removeLayer: function (layerDef) {
		var layer = layerDef.hasOwnProperty('layer') ? this._layerFromDef(layerDef) : layerDef;

		this._map.removeLayer(layer);

		L.Control.Layers.prototype.removeLayer.call(this, layer);
		return this;
	},

	clearLayers: function() {
		for (var id in this._layers) {
			this.removeLayer( this._getLayer(this._layers[id].id) );
		}
	},

	_layerFromDef: function(layerDef) {
		for (var id in this._layers) {
			//TODO add more conditions to comaparing definitions
			if(this._getLayer(this._layers[id].id).name === layerDef.name)
				return this._getLayer(this._layers[id].id).layer;
		}
	},

    _update: function() {
        this._groups = {};
        this._items = {};
        L.Control.Layers.prototype._update.call(this);
    },

	_addLayer: function (layerDef, overlay, group, isCollapsed) {

		if(!layerDef.layer)
			throw new Error('layer not defined in item: '+(layerDef.name||''));

		if( !(layerDef.layer instanceof L.Class) &&
			(layerDef.layer.type && layerDef.layer.args) ) {
			layerDef.layer = this._getPath(L, layerDef.layer.type).apply(L, layerDef.layer.args);
		}

		if(!layerDef.hasOwnProperty('id'))
			layerDef.id = L.stamp(layerDef.layer);

		if(layerDef.active)
			this._layersActives.push(layerDef.layer);

		var _layer = L.Util.extend(layerDef, {
			collapsed: isCollapsed,
			overlay: overlay,
			group: group
		});
		this._layers.push(_layer);

		if (this.options.autoZIndex && layerDef.layer && layerDef.layer.setZIndex) {
			this._lastZIndex++;
			layerDef.layer.setZIndex(this._lastZIndex);
		}
	},

    _getLayer: function (id) {
        for (var i = 0; i < this._layers.length; i++) {
            if (this._layers[i] && L.stamp(this._layers[i].layer) === parseInt(id)) {
                return this._layers[i];
            }
        }
    },

	_createItem: function(obj) {

		var self = this;

		var item, input, checked;

		item = L.DomUtil.create('div', this.className+'-item' +(obj.active?' active':'') );

		checked = this._map.hasLayer(obj.layer);

		if (obj.overlay) {
			input = L.DomUtil.create('input', this.className+'-selector');
			input.type = 'checkbox';
			input.defaultChecked = checked;
			//TODO name
		} else
			input = this._createRadioElement('leaflet-base-layers', checked);

		input.value = obj.id;
		input._layer = obj;

		L.DomEvent.on(input, 'click', function(e) {

			self._onInputClick();

			if(e.target.checked)
				self.fire('panel:selected', e.target._layer)
			else
				self.fire('panel:unselected', e.target._layer)

		}, this);

		var label = L.DomUtil.create('label', this.className+'-title');
		//TODO label.htmlFor = input.id;
		var title = L.DomUtil.create('span');
		title.innerHTML = obj.name||'';

		if(obj.icon) {
			var icon = L.DomUtil.create('i', this.className+'-icon');

			if(typeof obj.icon === 'string')
				icon.innerHTML = obj.icon || '';
			else
				icon.appendChild(obj.icon);

			label.appendChild(icon);
		}

		label.appendChild(input);
		label.appendChild(title);
		item.appendChild(label);

		if(this.options.buildItem)
		{
			var node = this.options.buildItem.call(this, obj); //custom node node or html string
			if(typeof node === 'string')
			{
				var tmp = L.DomUtil.create('div');
				tmp.innerHTML = node;
				item.appendChild(tmp.firstChild);
			}
			else
				item.appendChild(node);
		}

		this._items[ input.value ] = item;

		return item;
	},

	// IE7 bugs out if you create a radio dynamically, so you have to do it this hacky way (see http://bit.ly/PqYLBe)
	_createRadioElement: function (name, checked) {

		var radioHtml = '<input type="radio" class="'+this.className+'-selector" name="' + name + '"';
		if (checked) {
			radioHtml += ' checked="checked"';
		}
		radioHtml += ' />';

		var radioFragment = document.createElement('div');
		radioFragment.innerHTML = radioHtml;

		return radioFragment.firstChild;
	},

	_addItem: function (obj) {
		var self = this,
			label, input, icon, checked;

		var list = obj.overlay ? this._overlaysList : this._baseLayersList;

		if(obj.group) {
            if(!obj.group.hasOwnProperty('name'))
                obj.group = { name: obj.group };

            if(!this._groups[ obj.group.name ]) {
				var collapsed = false;
				if(obj.collapsed === true)
					collapsed = true;
                this._groups[ obj.group.name ] = this._createGroup( obj.group, collapsed );
			}

            list.appendChild(this._groups[obj.group.name]);
            list = this._groups[obj.group.name];
		}

		label = this._createItem(obj);

		list.appendChild(label);

		return label;
	},

    _createGroup: function ( groupdata, isCollapsed ) {

        var self = this,
        	groupdiv = L.DomUtil.create('div', this.className+'-group'),
            grouplabel, grouptit, groupexp;

        if(this.options.collapsibleGroups) {

			L.DomUtil.addClass(groupdiv, 'collapsible');

	        groupexp = L.DomUtil.create('i', this.className+'-icon', groupdiv);
			if(isCollapsed === true)
				groupexp.innerHTML = ' + ';
			else
				groupexp.innerHTML = ' - ';

	        L.DomEvent.on(groupexp, 'click', function() {
	            if ( L.DomUtil.hasClass(groupdiv, 'expanded') ) {
	                L.DomUtil.removeClass(groupdiv, 'expanded');
	                groupexp.innerHTML = ' + ';
	            } else {
	                L.DomUtil.addClass(groupdiv, 'expanded');
	                groupexp.innerHTML = ' - ';
	            }
	            self._updateHeight();
	        });

			if(isCollapsed === false)
				L.DomUtil.addClass(groupdiv, 'expanded');
	    }

       	grouplabel = L.DomUtil.create('label', this.className+'-grouplabel', groupdiv);
		grouptit = L.DomUtil.create('span', this.className+'-title', grouplabel);
		grouptit.innerHTML = groupdata.name;

        return groupdiv;
    },

	_onInputClick: function () {
		var i, input, obj,
		    inputs = this._form.getElementsByClassName(this.className+'-selector'),
		    inputsLen = inputs.length;

		this._handlingClick = true;

		for (i = 0; i < inputsLen; i++) {

			input = inputs[i];

			obj = this._getLayer(input.value);

			if (input.checked && !this._map.hasLayer(obj.layer)) {
				L.DomUtil.addClass(input.parentNode.parentNode, 'active');
				this._map.addLayer(obj.layer);

			} else if (!input.checked && this._map.hasLayer(obj.layer)) {
				L.DomUtil.removeClass(input.parentNode.parentNode, 'active');
				this._map.removeLayer(obj.layer);
			}
		}

		this._handlingClick = false;

		this._refocusOnMap();
	},


	_initLayout: function () {
		var container = this._container = L.DomUtil.create('div', this.className);

		//Makes this work on IE10 Touch devices by stopping it from firing a mouseout event when the touch is released
		container.setAttribute('aria-haspopup', true);

//		if (!L.Browser.touch) {
//			L.DomEvent
//				.disableClickPropagation(container)
//				.disableScrollPropagation(container);
//		} else
//			L.DomEvent.on(container, 'click', L.DomEvent.stopPropagation);

		//FIX IE 11 drag problem
		L.DomEvent.disableClickPropagation(container);

		if(this.options.className)
			L.DomUtil.addClass(container, this.options.className);

		this._form = L.DomUtil.create('form', this.className+'-list');

		this._updateHeight();

        if(this.options.compact)
			L.DomUtil.addClass(container, 'compact');
        else
			this._form.style.height = this._map.getSize().y+'px';

		//TODO supporto button mode
		/*if (this.options.button) {
			this.options.collapsed = true;
			L.DomUtil.addClass(container, this.className+'-button-collapse');
		}*/

		if (this.options.collapsed) {
			if (!L.Browser.android) {
				L.DomEvent
				    .on(container, 'mouseover', this._expand, this)
				    .on(container, 'mouseout', this._collapse, this);
			}
			var link = this._layersLink = L.DomUtil.create('a', this.className+'-toggle', container);
			link.href = '#';
			link.title = 'Layers';

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
		} else {
			this._expand();
		}

		this._baseLayersList = L.DomUtil.create('div', this.className+'-base', this._form);
		this._separator = L.DomUtil.create('div', this.className+'-separator', this._form);
		this._overlaysList = L.DomUtil.create('div', this.className+'-overlays', this._form);

        if(!this.options.compact)
        	L.DomUtil.create('div', this.className+'-margin', this._form);

        if(this.options.title) {
        	var titlabel = L.DomUtil.create('label', this.className+'-title');
        	titlabel.innerHTML = '<span>'+this.options.title+'</span>';
        	container.appendChild(titlabel);
        }

		container.appendChild(this._form);
	},

	_updateHeight: function(h) {
		h = h || this._map.getSize().y;
		if(this.options.compact)
			this._form.style.maxHeight = (h-30)+'px';
		else
			this._form.style.height = (h-30)+'px';
	},

	_expand: function () {
		L.DomUtil.addClass(this._container, 'expanded');
	},

	_collapse: function () {
		this._container.className = this._container.className.replace('expanded', '');
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
	}
});

//To support the on/off events
L.Control.PanelLayers.include(L.Evented.prototype);

L.control.panelLayers = function (baseLayers, overlays, options) {
	return new L.Control.PanelLayers(baseLayers, overlays, options);
};

return L.Control.PanelLayers;

});
