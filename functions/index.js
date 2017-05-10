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
exports.sendEmailConfirmation = functions.database.ref('/users/{uid}').onWrite(event => {
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



/// PUSH NOTIFICATION
exports.sendAddedToGroupNotification = functions.database.ref('/users/{uId}/groups/{adminName}').onWrite(event => {
  const uId = event.params.uId;
  const adminName = event.params.adminName;
  // If un-follow we exit the function.

  console.log('We have a new player added to :', adminName , 'group');

  // Get the list of device notification tokens.
  const getDeviceTokensPromise = admin.database().ref(`/users/${uId}/notificationTokens`).once('value');

  // Get the follower profile.
  const getFollowerProfilePromise = admin.auth().getUser(uId);

  return Promise.all([getDeviceTokensPromise, getFollowerProfilePromise]).then(results => {
    const tokensSnapshot = results[0];
    const user = results[1];

    // Check if there are any device tokens.
    if (!tokensSnapshot.hasChildren()) {
      return console.log('There are no notification tokens to send to.');
    }
    console.log('There are', tokensSnapshot.numChildren(), 'tokens to send notifications to.');
    console.log('Fetched follower profile', follower);

    // Notification details.
    const payload = {
      notification: {
        title: 'You have been added to a match!',
        body: `${uId.name} Please check your Squad App`,
      }
    };

    // Listing all tokens.
    const tokens = Object.keys(tokensSnapshot.val());

    // Send notifications to all tokens.
    return admin.messaging().sendToDevice(tokens, payload).then(response => {
      // For each message check if there was an error.
      const tokensToRemove = [];
      response.results.forEach((result, index) => {
        const error = result.error;
        if (error) {
          console.error('Failure sending notification to', tokens[index], error);
          // Cleanup the tokens who are not registered anymore.
          if (error.code === 'messaging/invalid-registration-token' ||
              error.code === 'messaging/registration-token-not-registered') {
            tokensToRemove.push(tokensSnapshot.ref.child(tokens[index]).remove());
          }
        }
      });
      return Promise.all(tokensToRemove);
    });
  });
});
