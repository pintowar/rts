import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import About from './modules/About';
import Monitor from './modules/Monitor';
import 'bootstrap/dist/css/bootstrap.css';
import './css/index.css';
import { Router, Route, hashHistory, IndexRoute } from 'react-router';

ReactDOM.render((
  <Router history={hashHistory}>
    <Route path="/" component={App}>
        <IndexRoute component={Monitor}/>
        <Route path="/about" component={About}/>
        <Route path="/monitor" component={Monitor}/>
    </Route>
  </Router>
), document.getElementById('root'))
