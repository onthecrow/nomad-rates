#!/usr/bin/env bash
# The Firebase config files are not in the repository. CI builds the apps with placeholders: they
# compile and launch the same way, but can't reach Firebase. Existing files are left untouched.
set -euo pipefail
cd "$(dirname "$0")/../.."

android_config=androidApp/google-services.json
if [ ! -f "$android_config" ]; then
  cat > "$android_config" <<'JSON'
{
  "project_info": {
    "project_number": "000000000000",
    "project_id": "nomadrates-ci",
    "storage_bucket": "nomadrates-ci.appspot.com"
  },
  "client": [
    {
      "client_info": {
        "mobilesdk_app_id": "1:000000000000:android:0000000000000000",
        "android_client_info": { "package_name": "com.onthecrow.nomadrates" }
      },
      "oauth_client": [],
      "api_key": [{ "current_key": "ci-placeholder" }],
      "services": { "appinvite_service": { "other_platform_oauth_client": [] } }
    },
    {
      "client_info": {
        "mobilesdk_app_id": "1:000000000000:android:0000000000000001",
        "android_client_info": { "package_name": "com.onthecrow.nomadrates.dev" }
      },
      "oauth_client": [],
      "api_key": [{ "current_key": "ci-placeholder" }],
      "services": { "appinvite_service": { "other_platform_oauth_client": [] } }
    }
  ],
  "configuration_version": "1"
}
JSON
  echo "Created a placeholder $android_config"
fi

ios_config=iosApp/Configuration/GoogleService-Info-Debug.plist
if [ ! -f "$ios_config" ]; then
  cat > "$ios_config" <<'PLIST'
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
  <key>API_KEY</key>
  <string>ci-placeholder</string>
  <key>GCM_SENDER_ID</key>
  <string>000000000000</string>
  <key>PLIST_VERSION</key>
  <string>1</string>
  <key>BUNDLE_ID</key>
  <string>com.onthecrow.nomadrates.dev</string>
  <key>PROJECT_ID</key>
  <string>nomadrates-ci</string>
  <key>STORAGE_BUCKET</key>
  <string>nomadrates-ci.appspot.com</string>
  <key>IS_ADS_ENABLED</key>
  <false/>
  <key>IS_ANALYTICS_ENABLED</key>
  <false/>
  <key>IS_APPINVITE_ENABLED</key>
  <false/>
  <key>IS_GCM_ENABLED</key>
  <false/>
  <key>IS_SIGNIN_ENABLED</key>
  <false/>
  <key>GOOGLE_APP_ID</key>
  <string>1:000000000000:ios:0000000000000000</string>
</dict>
</plist>
PLIST
  echo "Created a placeholder $ios_config"
fi
