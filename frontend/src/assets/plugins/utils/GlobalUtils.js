
+function () {
    'use strict'

    var GlobalUtils = {
        Event: {
            topBarTabDisplay        : 'topBarTabDisplay',
            openLeftPane            : 'openLeftPane',
            closeLeftPane           : 'closeLeftPane',
            displayLeftPane         : 'displayLeftPane',
            initializedLeftPane     : 'initializedLeftPane',
            filterChange            : 'filterChange',
            snapshotDateNeedChange  : 'snapshotDateNeedChange',
            granularityChange       : 'granularityChange',
            popupclose              : 'popupclose',
            editStructureClicked    : 'editStructureClicked',
            rendererConfigChanged   : 'rendererConfigChanged',
            chartConfigChanged      : 'chartConfigChanged',
            farmSelectChanged       : 'farmSelectChanged',
            datasetSelectChanged    : 'datasetSelectChanged',
            reloadPageAfterConfig   : 'reloadPageAfterConfig',
            configChangedFarm       : 'configChangedFarm',
            configChangedMap        : 'configChangedMap',
            configChangedDataset    : 'configChangedDataset',
            configChangedStructure  : 'configChangedStructure',
            configChangedRenderer   : 'configChangedRenderer',
            configChangedChart      : 'configChangedChart',
            configChangedDatasource : 'configChangedDatasource',
            datasourceDetailChanged : 'datasourceDetailChanged',
            configAddedExtendColumn : 'configAddedExtendColumn'
        },
        structureTable: {
            idxOfKInStructureTable: 0,
            idxOfConverterInStructureTable: 1,
            idxOfConverterRuleInStructureTable: 2,
            idxOfFormatInStructureTable: 3,
            idxOfValidationInStructureTable: 4,
            idxOfValidationRuleInStructureTable: 5,
            idxOfMappingInStructureTable: 6,
            idxOfTypeInStructureTable: 7,
            idxOfAliasInStructureTable: 8,
            idxOfExtendInStructureTable: 9,
            idxOfManualInStructureTable: 10,
            idxOfValueTypeInStructureTable: 11,
            idxOfValueInStructureTable: 12,
            idxOfCreateAtInStructureTable: 13
        },
        farmId: null, //current selected farm
        datasetId: null, //current selected dataset
        currentTabDataset: null, //current display datasets tab is related with which dataset json
        currentFilterJson: { //current filter pane values
            granularity: 'MINUTE',
            fromDate: null,
            toDate: null,
            dynamicFilters: null
        },
        currentSnapshotDates: null, //current snapshot date value list
        currentSelectedPolygon: { //current selected polygon information
            block: null,
            tunnel: null
        },
        currentCalendarStatus: null, //current calendar status to search
        beginLoading: function() {
            if (!this.loading()){
                this.$loading = $('<div class="loading"></div>');
                $('body').append(this.$loading);
            }
        },
        endLoading: function() {
            if (this.loading()){
                this.$loading.remove();
                this.$loading = null;
            }
        },
        loading: function() {
            if (this.$loading){
                return true;
            }
            else{
                return false;
            }
        },
        generateColor: function(str){
            var i = 0;
            var hash = 0;
            for (i = 0; i < str.length; i++) {
                hash = str.charCodeAt(i) + ((hash << 5) - hash);
            }
            var color = '#';
            for (i = 0; i < 3; i++) {
                var value = (hash >> (i * 8)) & 0xFF;
                color += ('00' + value.toString(16)).substr(-2);
            }
            return color;
        },
        handlerError: function(data) {
            if (data){
                if(data.handlerError === true){
                    var info = null;
                    if(data.data && data.data != null){
                        info = L.Util.template(Templates.handlerErrorTpl(), {
                            code: data.code,
                            message: data.message,
                            data: JSON.stringify(data.data)
                        });
                    }
                    else{
                        info = L.Util.template(Templates.handlerErrorWithoutDataTpl(), {
                            code: data.code,
                            message: data.message
                        });
                    }

                    $.alert({
                        columnClass: 'col-md-12',
                        icon: 'fa fa-warning',
                        title: 'Error',
                        type: 'red',
                        content: info
                    });

                    GlobalUtils.endLoading();
                    return true;
                }
            }
            else {
                return false;
            }
        },
        syntaxHighlight: function(json) {
            if (typeof json != 'string') {
                json = JSON.stringify(json, undefined, 2);
            }
            json = json.replace(/&/g, '&').replace(/</g, '<').replace(/>/g, '>');
            return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function(match) {
                var cls = 'number';
                if (/^"/.test(match)) {
                    if (/:$/.test(match)) {
                        cls = 'key';
                    } else {
                        cls = 'string';
                    }
                } else if (/true|false/.test(match)) {
                    cls = 'boolean';
                } else if (/null/.test(match)) {
                    cls = 'null';
                }
                return '<span class="' + cls + '">' + match + '</span>';
            });
        },
        showInfoModal: function(htmlStr) {
            $('#infoModalContent').html('');
            $('#infoModalContent').html(htmlStr);
            $('#infoModal').modal('show');
        },
        insertAtCursor: function(elem, value){
             //IE
             if (document.selection) {
                 elem.focus();
                 var sel = document.selection.createRange();
                 sel.text = value;
                 sel.select();
             }
             //FireFox, Chrome
             else if (elem.selectionStart || elem.selectionStart == '0') {
                 var startPos = elem.selectionStart;
                 var endPos = elem.selectionEnd;

                 var restoreTop = elem.scrollTop;
                 elem.value = elem.value.substring(0, startPos) + value + elem.value.substring(endPos, elem.value.length);

                 if (restoreTop > 0) {
                    elem.scrollTop = restoreTop;
                 }

                 elem.focus();
                 elem.selectionStart = startPos + value.length;
                 elem.selectionEnd = startPos + value.length;
             } else {
                 elem.value += value;
                 elem.focus();
             }
        }
    };

    $.extend($, {
        sync: function (options) {
            var defaultSetting = {
                checkError: true,
                checkAuth: true,
                timeout: 20000,
                loading: true,
                cache : false,
                contentType: "application/json"
            };
            var opt = $.extend(true, defaultSetting, options),
                type = (opt.type || 'get').toLowerCase(),
                data = opt.data;
            if(type == 'post' && data){
                data = JSON.stringify(data);
            }
            var ajaxData = {
                url: opt.url,
                type: type,
                data: data,
                cache : opt.cache,
                contentType: opt.contentType,
                success:function(data, status, xhr){
                    if(opt.loading){
                        GlobalUtils.endLoading();
                    }
                    if (opt.checkError) {
                        if(GlobalUtils.handlerError(data)){
                            return;
                        }
                    }
                    if (opt.checkAuth) {
                        var sessionstatus = xhr.getResponseHeader("session-status");
                        if(sessionstatus=="timeout"){
                            window.location.href = '/login-form';
                            return;
                        }
                    }
                    opt.success && opt.success(data, status, xhr);
                },
                error : function(data) {
                    if(opt.loading){
                        GlobalUtils.endLoading();
                    }
                    if (opt.checkError) {
                        if(GlobalUtils.handlerError(data)){
                            return;
                        }
                    }
                    console.error("Error for url: " + opt.url + ', data.statusText: ' + data.statusText);
                    opt.error && opt.error(data);
                }
            };
            if (opt.loading) {
                GlobalUtils.beginLoading();
            }
            $.ajax(ajaxData);
        }
    });

    window.GlobalUtils = GlobalUtils;
}()