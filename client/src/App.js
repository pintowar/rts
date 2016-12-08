import React, { Component } from 'react';
import Timeline from './Timeline.js';
import logo from './images/logo.svg';
import './css/App.css';
import { SERVER_URL } from './config';
import { Button } from 'react-bootstrap';

const options = {
  width: '90%',
  height: '200px',
  // stack: false,
  showMajorLabels: true,
  // showCurrentTime: true,
  zoomMin: 1000000,
  // type: 'background',
  groupOrder: function (a, b) { return a.value - b.value; },
  format: {
    minorLabels: { minute: 'h:mma', hour: 'ha' }
  },
  orientation: { axis: 'top' },
  // item: { axis: 'top' }
}

class App extends Component {

  render() {
    let channel = SERVER_URL + 'task/channel'
    let url = SERVER_URL + 'task.json'
    let startSolverUrl = SERVER_URL + 'task/start-solver'
    let stopSolverUrl = SERVER_URL + 'task/stop-solver'
    let event = 'task'

    let solverAction = (url) => fetch(url).then(r => r.json().then(s => console.log(s)))
                                          .catch(error => console.error('Error connecting to server: ' + error));
    let startAction = () => solverAction(startSolverUrl)
    let stopAction = () => solverAction(stopSolverUrl)
    return (
      <div className="App">
        <div className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h2>Welcome to React</h2>
        </div>
        <Button bsStyle="success" bsSize="small" onClick={startAction} >Start Solving</Button>
        <Button bsStyle="danger" bsSize="small" onClick={stopAction} >Stop Solving</Button>
        <Timeline options={options} url={url} channel={channel} event={event} />
      </div>
    );
  }
}

export default App;
