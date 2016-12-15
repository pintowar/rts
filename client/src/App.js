import React, { Component } from 'react';
import Timeline from './Timeline.js';
import goes from './images/logo_goes.png'
import './css/App.css';
import { SERVER_URL } from './config';
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
    let channel = SERVER_URL + 'task/channel'
    let url = SERVER_URL + 'task.json'
    let startUrl = SERVER_URL + 'task/start-solver'
    let stopUrl = SERVER_URL + 'task/stop-solver'
    let event = 'task'

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
              <Timeline options={options} url={url} channel={channel} event={event}
                         start={startUrl} stop={stopUrl}/>
          </div>
    )
  }
}

export default App;
