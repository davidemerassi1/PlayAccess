package it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.facelandmarks

class MissingFaceLandmarkException: Exception {
    constructor(): super() {}
    constructor(message: String?): super(message) {}
}