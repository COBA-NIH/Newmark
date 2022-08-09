// Script Authors: Mike Nelson*, Ellen TA Dobson
// *primary author

// This script allows exports each numbered tissue section as an independent .tiff file.


def server = getCurrentServer()
double downsample = 1.0
bounding = getAnnotationObjects().findAll{it.getPathClass() == getPathClass("Bounding")}

resolveHierarchy()
bounding.each{
    roi = it.getROI()
    name = measurement(it, "Number")
    path = buildFilePath(PROJECT_BASE_DIR, 'Export',getProjectEntry().getImageName()+'_'+name+'.ome.tif')

    def requestROI = RegionRequest.createInstance(server.getPath(), downsample, roi)
    writeImageRegion(server, requestROI, path)
}