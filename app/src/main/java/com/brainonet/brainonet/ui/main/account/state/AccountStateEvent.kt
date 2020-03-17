package com.brainonet.brainonet.ui.main.account.state

sealed class AccountStateEvent{

    class GetAccountPropertiesEvent: AccountStateEvent()

    data class UpdateAccountPropertiesEvent(
        val first_name: String,
        val last_name: String
    ): AccountStateEvent()

    class None: AccountStateEvent()
}