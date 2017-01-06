import React, { Component } from 'react';
import Timeline from './Timeline.js';
import Timeseries from './Timeseries.js';
import Controlpanel from './Controlpanel.js';
import goes from './images/logo_goes.png'
import './css/App.css';
import { Image, Navbar } from 'react-bootstrap';
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'

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
    margin: {item: 1}
    // item: { axis: 'top' }
}

class App extends Component {

    componentDidMount() {
        let socket = new SockJS('/stomp')
        let client = Stomp.over(socket)
        client.debug = null
        let self = this
        client.connect({}, function() {
            client.subscribe("/topic/solution", function(message) {
                self.changeData(JSON.parse(message.body))
            });
        });
    }

    changeData(data) {
        let components = [this.refs.timeline, this.refs.timeseries, this.refs.controlpanel]
        components.forEach(it => it.changeData(data))
    }

    render() {
        let task_url = '/task.json'
        let solutions_url = '/task/solutions.json'
        let startUrl = '/task/start-solver'
        let stopUrl = '/task/stop-solver'

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
                <Controlpanel ref="controlpanel" url={task_url} start={startUrl} stop={stopUrl}/>
                <Timeline ref='timeline' options={options} url={task_url} />
                <Timeseries ref='timeseries' url={solutions_url} />
            </div>
        )
    }
}

export default App;
