(function(window){


    var MobileRecorder = function() {


        var recordFileFQN = null;
        var fName = "recording";
        var extension  =  ".amr";
        var theRecorder, thePlayer;
        var _fileReadyCallback = null;
        var recordFileName = "tmpRecord";

        function saveAudio(file) {

            MobileRecorder.blob = file;
            if(_fileReadyCallback) _fileReadyCallback();

        }

        function onSuccessGetFS(fileSystem) {
            fileSystem.root.getFile(recordFileName, {create: false, exclusive: false}, saveAudio, onFail);

        }
        function onMediaCallSuccess(){};
        function onMediaCallError(){};


        function onFail(){

        };

        this.loadRecordingFile = function() {
            window.requestFileSystem(LocalFileSystem.PERSISTENT, 0, onSuccessGetFS, onFail);
        };

        this.getSrcName = function (index){
            return fName + index + extension;

        }
        this.record = function (index) {
            if (theRecorder) {
                theRecorder.release();
            }
            recordFileName = this.getSrcName(index);
            theRecorder = new Media(recordFileName, onMediaCallSuccess, onMediaCallError);
            theRecorder.startRecord();
        };

        this.stop = function (callback) {
            if (theRecorder) {
                theRecorder.stopRecord();
                _fileReadyCallback = callback;
                this.loadRecordingFile()
            }
        };

        this.clear = function () {
            if(thePlayer)
                thePlayer.release();
            thePlayer = null;
            if (theRecorder)
                theRecorder.release();
            theRecorder = null;

        };

        this.play = function (index) {
            thePlayer = new Media(this.getSrcName(index), onMediaCallSuccess, onMediaCallError);
            if (thePlayer) {
                thePlayer.play();
            }
        }



    }
    window.MobileRecorder = MobileRecorder;

})(window);