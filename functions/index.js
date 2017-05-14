//var functions = require('firebase-functions');
var admin = require('firebase-admin');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });


/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/// USER EMAIL REG NOTIFICATION

'use strict';

const functions = require('firebase-functions');
const nodemailer = require('nodemailer');

admin.initializeApp(functions.config().firebase);

// Configure the email transport using the default SMTP transport and a GMail account.
// For other types of transports such as Sendgrid see https://nodemailer.com/transports/
// TODO: Configure the `gmail.email` and `gmail.password` Google Cloud environment variables.
const gmailEmail = encodeURIComponent(functions.config().gmail.email);
const gmailPassword = encodeURIComponent(functions.config().gmail.password);
const mailTransport = nodemailer.createTransport(
    `smtps://${gmailEmail}:${gmailPassword}@smtp.gmail.com`);

// Sends an email confirmation when a user changes his mailing list subscription.
exports.sendEmailConfirmation = functions.database.ref('/users/{uid}/uid').onWrite(event => {
  const snapshot = event.data;
  const val = snapshot.val();

  if (!snapshot.changed('email')) {
    return;
  }

  const mailOptions = {
    from: '"Squad Dev Team" <noreply@firebase.com>',
    to: val.email
  };


  mailOptions.subject = 'Thanks for using Squad, and welcome! :-)';
  mailOptions.text = 'Thanks you for subscribing to Squad! Regards The Squad Team.';
  return mailTransport.sendMail(mailOptions).then(() => {
    console.log('New sign up confirmation email sent to:', val.email);
  });
});

/*
// EMAIL TO SAY YOU HAVE BEEN ADDED TO A GROUP
exports.sendEmailGroupConf = functions.database.ref('/users/{uid}').onWrite(event => {
  const snapshot = event.data;
  const val = snapshot.val();

  if (!snapshot.changed('email')) {
    return;
  }

  const mailOptions = {
    from: '"Squad Dev Team" <noreply@firebase.com>',
    to: val.email
  };


  // The user unsubscribed to the newsletter.
  mailOptions.subject = 'You have been invited to a Match';
  mailOptions.text = 'Please check your Squad App.';
  return mailTransport.sendMail(mailOptions).then(() => {
    console.log('Added to group confirmation email sent to:', val.email);
  });
  });

*/

///push try
/// PUSH NOTIFICATION - CODE REF - https://github.com/firebase/functions-samples/tree/master/fcm-notifications
// DEVELOPED FROM ABOVE
exports.sendNotification = functions.database.ref('/users/{uid}/groups')
    .onWrite(event => {

        const user = event.data.current.val();
        const senderUid = user.groups.groupId;
        const receiverUid = user.uid;
        const promises = [];


        if (senderUid == receiverUid) {
            //if sender is receiver, don't send notification
            promises.push(event.data.current.ref.remove());
            return Promise.all(promises);
        }

        const getRefreshedTokenPromise = admin.database().ref(`/users/${receiverUid}/refreshedToken`).once('value');
        const getReceiverUidPromise = admin.auth().getUser(receiverUid);

        return Promise.all([getRefreshedTokenPromise, getReceiverUidPromise]).then(results => {
            const refreshedToken = results[0].val();
            const receiver = results[1];
            console.log('notifying ' + receiverUid  +  ' from ' + senderUid);

              const payload = {
        notification: {
          title: 'You have a new follower!' ,
          body: ` Check your Squad App for details.`
        }
  };
            admin.messaging().sendToDevice(refreshedToken, payload)
                .then(function (response) {
                    console.log("Successfully sent message:", response);
                })
                .catch(function (error) {
                    console.log("Error sending message:", error);
                });
        });
});
