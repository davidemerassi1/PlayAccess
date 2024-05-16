package it.unimi.di.ewlab.iss.common.configuratorsmodels

import java.io.Serializable

sealed class ViewState : Serializable {
    object Loading : ViewState()
    data class Done(val model: BaseModel = BaseModel()) : ViewState()
    data class Error(@Transient val exception: Throwable = Exception(), val message: String? = null, val showWarning: Boolean? = null) : ViewState()
}