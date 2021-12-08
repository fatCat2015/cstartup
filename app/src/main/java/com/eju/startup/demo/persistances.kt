package com.eju.startup.demo

import android.content.Context

const val ALREADY_AGREED_PRIVACY_POLICY = "already_agreed_privacy_policy"

val sp = App.application.getSharedPreferences("dadada",Context.MODE_PRIVATE)

fun hasAlreadyAgreedPrivacyPolicy() = sp.getBoolean(ALREADY_AGREED_PRIVACY_POLICY,false)

fun agreedPrivacyPolicy(flag:Boolean){
    sp.edit().putBoolean(ALREADY_AGREED_PRIVACY_POLICY,flag).commit()
}