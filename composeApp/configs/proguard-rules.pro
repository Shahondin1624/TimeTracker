-keep class androidx.compose.runtime.** { *; }
-keep class androidx.collection.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class androidx.compose.ui.text.platform.ReflectionUtil { *; }

# We're excluding Material 2 from the project as we're using Material 3
-dontwarn androidx.compose.material.**

# Kotlinx coroutines rules seems to be outdated with the latest version of Kotlin and Proguard
-keep class kotlinx.coroutines.** { *; }

# Suppress warnings for Jakarta EE classes that are not needed for desktop application
-dontwarn jakarta.**
-dontwarn ch.qos.logback.classic.helpers.MDCInsertingServletFilter
-dontwarn ch.qos.logback.classic.selector.servlet.**
-dontwarn ch.qos.logback.classic.servlet.**
-dontwarn ch.qos.logback.core.net.LoginAuthenticator
-dontwarn ch.qos.logback.core.net.SMTPAppenderBase
-dontwarn ch.qos.logback.core.status.ViewStatusMessagesServletBase
-dontwarn org.codehaus.janino.**
-dontwarn org.codehaus.commons.compiler.**
