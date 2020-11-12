// Script Authors: Mike Nelson*, Ellen TA Dobson
// *primary author

// (Optional) This script allows the user to manually reorder the tissue sections if needed.


list = getSelectedObjects()
names = []
list.each{
    names<<it.getName()
}
names.sort()

list.eachWithIndex{i,x->
    n = i.getName() as double
    bounding = getAnnotationObjects().findAll{it.getPathClass() == getPathClass("Bounding")}
    box = bounding.find{measurement(it, "Number") == n}
    box.getMeasurementList().putMeasurement("Number", names[x] as double)
    i.setName(names[x])
    
}