sap.ui.controller("view.StartView", {

/**
* Called when a controller is instantiated and its View controls (if available) are already created.
* Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
* @memberOf view.StartView
*/
//	onInit: function() {
//
//	},

/**
* Similar to onAfterRendering, but this hook is invoked before the controller's View is re-rendered
* (NOT before the first rendering! onInit() is used for that one!).
* @memberOf view.StartView
*/
//	onBeforeRendering: function() {
//
//	},

/**
* Called when the View has been rendered (so its HTML is part of the document). Post-rendering manipulations of the HTML could be done here.
* This hook is the same one that SAPUI5 controls get after being rendered.
* @memberOf view.StartView
*/
//	onAfterRendering: function() {
//
//	},

/**
* Called when the Controller is destroyed. Use this one to free resources and finalize activities.
* @memberOf view.StartView
*/
//	onExit: function() {
//
//	}


handleStart: function(btn)  {
    sap.m.MessageBox.alert('Start', {title: 'Status'});
    btn.setText("Stop");
},

handleStop: function(btn) {
    sap.m.MessageBox.alert('Stop', {title: 'Status'});
    btn.setText("Start");
},

pressBtn: function(oEvent) {
    var status = oEvent.getText();
    if (status === "Start") {
        this.handleStart(oEvent);
    } else if (status === "Stop") {
        this.handleStop(oEvent);
    }
    
}


});