// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

$('.navbar-collapse a').click(function () {
  $(".navbar-collapse").collapse('hide');
});

$(function () {
  $(document).scroll(function () {
    var $nav = $(".fixed-top");
    $nav.toggleClass('scrolled', $(this).scrollTop() > $nav.height());
  });
});

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

async function initializeComments() {
  await getComments();
  const response = await fetch('/auth-status');
  const authData = await response.json();

  if (!authData.isLoggedIn) {
    const commentCreateForm = document.getElementById('comment-form');
    commentCreateForm.hidden = true;
    const logoutContainer = document.getElementById('logout-container');
    logoutContainer.hidden = true;
    const loginUrl = document.getElementById('login-url');
    loginUrl.innerHTML = `<a href="${authData.authURL}"> here </a>`;
  } else {
    const loginContainer = document.getElementById('login-container');
    loginContainer.hidden = true;
    const logoutUrl = document.getElementById('logout-url');
    logoutUrl.innerHTML = `<a href="${authData.authURL}"> here </a>`;
  }
}

async function getComments() {
  const response = await fetch('/comments');
  const comments = await response.json();
  const commentsListElement = document.getElementById('comments-container');

  for (let i = 0; i <comments.length; ++i) {
    commentsListElement.appendChild(
      createComment(comments[i].message, comments[i].author)
    );
  }
}

function createComment(message, author) {
  const comment = createDivElement();
  comment.append(createHeadingElement(author));
  comment.append(createParaElement(message));
  return comment;
}

/** Creates a <p> element containing text. */
function createParaElement(text) {
  const paraElement = document.createElement('p');
  paraElement.innerText = text;
  return paraElement;
}

/** Creates a <strong> element containing text. */
function createHeadingElement(text) {
  const headingElement = document.createElement('strong');
  headingElement.innerText = text;
  return headingElement;
}

/** Creates a <div> element containing text. */
function createDivElement(text) {
  return document.createElement('div');
}
