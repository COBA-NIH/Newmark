// Script Authors: Mike Nelson*, Ellen TA Dobson
// *primary author

// This script sets a fixed-size bounding box around detected tissue sections and orders them
// from left-to-right, top/down based on position on the slide.


// Before running these series of scripts:
//  Script01-detectTissues-fixBoundingBoxes-numberOrder.groovy
//  (optional) Script02-optional-manual-reorder.groovy
//  Script03-export.groovy
// One must first define a 'classifier' in QuPath (Classify > Pixel Classification > Create Thresholder… ).
// Also, add the label ‘Tissue’ (just go to Annotations tab and then ‘…’ button and ‘add class’).
// Use the term “Rough tissue” for the classifier name, as that is how it is referred to in the scripts.
// Finally, be sure to 'Create Objects' before running the scripts.


//SETUP
//Change the size of the bounding box here if your tissue slices are not fitting.
border = 6500

/////////////////////
setImageType('BRIGHTFIELD_H_DAB');
setColorDeconvolutionStains('{"Name" : "H-DAB default", "Stain 1" : "Hematoxylin", "Values 1" : "0.65111 0.70119 0.29049 ", "Stain 2" : "DAB", "Values 2" : "0.26917 0.56824 0.77759 ", "Background" : " 255 255 255 "}');
createAnnotationsFromPixelClassifier("Rough tissue", 80000.0, 0.0, "SPLIT")


/////////////////////////
//NUMBER THE TISSUE SLICES

List points = []
ymax = 0
ymin = 0
//get everything that is not a bounding box
//based on my understanding, this should be tissue objects
getAnnotationObjects().findAll{it.getPathClass() != getPathClass("Bounding")}.each{

    //Go through each tissue object and add it's centroids to the points list
    //Keep track of the max and min Y values
    x = it.getROI().getCentroidX()
    y = it.getROI().getCentroidY()
    if (y > ymax){ymax = y}
    if (y < ymin){ymax = y}
    //Adding "it" here as the 3rd object in the list keeps track of the annotation associated with the XY point
    points << [x,y,it]
}
print points
print points.size()
midpoint = (ymax+ymin)/2
pointsHigh = []
pointsLow = []
//use the midpoint to separate points into top and bottom groups
points.each{
//access the second position, the Y value
    if(it.get(1)>=midpoint){
        pointsLow << it
    }else {pointsHigh << it }

}

//sort in ascending X order
pointsLow.sort({a,b -> a[0]<=>b[0]}).reverse()
pointsHigh.sort({a,b -> a[0]<=>b[0]}).reverse()
//Starting at 1, number all of the sorted annotations that are associated with the points
i=1
pointsHigh.each{
    it[2].setName(i.toString())
    i++
}
pointsLow.each{
    it[2].setName(i.toString())
    i++
}

///////////////////////
//CREATE BOUNDING BOXES

removeObjects(getAnnotationObjects().findAll{it.getPathClass() == getPathClass("Bounding")},true)

tissue = getAnnotationObjects()

boxes = []
tissue.each{
    roi = it.getROI()
    name = it.getName() as double
    //print name
    def plane = ImagePlane.getPlane(0, 0)
    boundingROI = ROIs.createRectangleROI(
                    roi.getCentroidX()-border/2, //top left corner
                    roi.getCentroidY()-border/2, //top left corner
                    border, //width
                    border, //height
                    plane)
    boundingAnnotation = PathObjects.createAnnotationObject(boundingROI, getPathClass("Bounding"))
    //print roi.getBoundsHeight()
    //print roi.getBoundsWidth()
    boundingAnnotation.getMeasurementList().putMeasurement("Number", name)
    boxes << boundingAnnotation
}
addObjects(boxes)
resolveHierarchy()

print "All done!"