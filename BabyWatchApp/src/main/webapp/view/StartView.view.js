sap.ui.jsview("view.StartView", {

	/** Specifies the Controller belonging to this View. 
	* In the case that it is not implemented, or that "null" is returned, this View does not have a Controller.
	* @memberOf view.StartView
	*/ 
	getControllerName : function() {
		return "view.StartView";
	},

	/** Is initially called once after the Controller has been instantiated. It is the place where the UI is constructed. 
	* Since the Controller is given to this method, its event handlers can be attached right away. 
	* @memberOf view.StartView
	*/ 
	createContent : function(oController) {
	    
        var oButton = new sap.m.Button('startBtn', {
            text: "Start"
        });
        
        oButton.attachPress(function() {oController.pressBtn(this);});

		return new sap.m.Page({
			title: "Baby Watch",
			content: [
				oButton
			]
		});
	}

});
