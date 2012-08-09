var smallTransparentGif = "";
function fixupIEPNG(strImageID, transparentGif) 
{
    smallTransparentGif = transparentGif;
    if (windowsInternetExplorer)
    {
        var img = document.getElementById(strImageID);
        if (img)
        {
            var src = img.src;
            img.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + src + "', sizingMethod='scale')";
            img.src = transparentGif;
            img.attachEvent("onpropertychange", imgPropertyChanged);
        }
    }
}

var windowsInternetExplorer = false;
function detectBrowser()
{
    windowsInternetExplorer = false;
    var appVersion = navigator.appVersion;
    if ((appVersion.indexOf("MSIE") != -1) &&
        (appVersion.indexOf("Macintosh") == -1))
    {
        windowsInternetExplorer = true;
    }
}

var inImgPropertyChanged = false;
function imgPropertyChanged()
{
    if ((window.event.propertyName == "src") && (! inImgPropertyChanged))
    {
        inImgPropertyChanged = true;
        var el = window.event.srcElement;
        if (el.src != smallTransparentGif)
        {
            el.filters.item(0).src = el.src;
            el.src = smallTransparentGif;
        }
        inImgPropertyChanged = false;
    }
}

function onPageLoad()
{
    detectBrowser();
    fixupIEPNG("id1", "SAND1.0HelpFile_files/transparent.gif");
    fixupIEPNG("id2", "SAND1.0HelpFile_files/transparent.gif");
    fixupIEPNG("id3", "SAND1.0HelpFile_files/transparent.gif");
    fixupIEPNG("id4", "SAND1.0HelpFile_files/transparent.gif");
    fixupIEPNG("id5", "SAND1.0HelpFile_files/transparent.gif");
    fixupIEPNG("id6", "SAND1.0HelpFile_files/transparent.gif");
    return true;
}

