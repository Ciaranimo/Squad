//var functions = require('firebase-functions');
var admin = require('firebase-admin');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
/// USER EMAIL REG NOTIFICATION
// Code Ref - https://github.com/firebase/functions-samples 
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


///push try
/// PUSH NOTIFICATION - CODE REF - https://github.com/firebase/functions-samples/tree/master/fcm-notifications
// DEVELOPED FROM ABOVE
exports.sendNotification = functions.database.ref('/users/{uid}/')
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
          title: 'You have been added to a group!',
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
