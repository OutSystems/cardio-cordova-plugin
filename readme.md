# OutSystems card.io cordova plugin

The purpose of this plugin is to leverage Hybrid Applications with [card.io](https://www.card.io/) features. With this plugin the users can scan credit cards in your mobile app.

As with all the cordova plugins, the plugin isn't available until the execution of `deviceready` event.

```javascript
document.addEventListener("deviceready", onDeviceReady, false);
function onDeviceReady() {
    console.log(FileTransfer);
}
```

## Supported Platforms

- iOS
- Android

## Installation
- Run the following command:

```shell
    cordova plugin add https://bitbucket.org/uxmobile/cardio-cordova-plugin.git
```
### Android

An additional step has to be done for Android.
In your `build.gradle`, add:

```
repositories {
    mavenCentral()
}
```

After that, you just need to add _CardIO_ as a dependency to your application:

```
dependencies {
    compile 'io.card:android-sdk:5.1.2'
}
```

## CardIOPlugin

### scanCard()

#### Syntax:

```javascript
function scanCard(successCallback, errorCallback, requireExpiry, requireCvv, requirePostalCode)
```

#### Parameters:
* `successCallback` A callback function invoked with an argument:
  * `scanResult`:
    ```javascript
    {
      "cardType":"",
      "expiryMonth":9,
      "expiryYear":2016,
      "postalCode":"1234-123",
      "cvv":"123",
      "redactedCardNumber":"••••••••••••1234",
      "rawCardNumber":"1234123412341234"
    }
    ```
* `errorCallback` A callback function invoked with an argument:
  * `error`:
    ```javascript
    {
      "error_code":0,
      "error_message": "";
    }
    ```
* `requireExpiry` _bool_ Require expiration date to be entered?
* `requireCvv` _bool_ Require CVV code to be entered?
* `requirePostalCode` _bool_ Require Postal Code (zip code) to be entered?


#### Example

```javascript
plugin.cardio.scanCard(
  function(scanResult){
    console.log(scanResult)
  },
  function(error){
    console.log(error)
  }, true, true, true);
```

---

#### Contributors
- OutSystems - Mobility Experts
    - João Gonçalves, <joao.goncalves@outsystems.com>
    - Rúben Gonçalves, <ruben.goncalves@outsystems.com>
    - Vitor Oliveira, <vitor.oliveira@outsystems.com>

#### Document author
- João Gonçalves, <joao.goncalves@outsystems.com>

### Copyright OutSystems, 2015

---

LICENSE
=======


[The MIT License (MIT)](http://www.opensource.org/licenses/mit-license.html)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
