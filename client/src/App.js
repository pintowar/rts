import React, { Component } from 'react';
import Timeline from './Timeline.js';
import Timeseries from './Timeseries.js';
import goes from './images/logo_goes.png'
import './css/App.css';
import { Image, Navbar } from 'react-bootstrap';

const options = {
  width: '100%',
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
    let channel = '/task/channel'
    let task_url = '/task.json'
    let solutions_url = '/task/solutions.json'
    let startUrl = '/task/start-solver'
    let stopUrl = '/task/stop-solver'
    let event = 'task'
    let source = new EventSource(channel);

    return (
        <div id="parent">
            <Navbar>
                <Navbar.Header>
                  <Navbar.Brand><Image src={goes} /></Navbar.Brand>
                  <Navbar.Toggle />
                </Navbar.Header>
                <Navbar.Collapse>
                  <Navbar.Text>Thiago Oliveira</Navbar.Text>
                  <Navbar.Text pullRight>Task Planner</Navbar.Text>
                </Navbar.Collapse>
              </Navbar>
              <Timeline options={options} url={task_url} source={source} event={event} start={startUrl} stop={stopUrl} />
              <Timeseries source={source} url={solutions_url} event={event} />
          </div>
    )
  }
}

export default App;
