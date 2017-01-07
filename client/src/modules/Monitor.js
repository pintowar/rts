import React, { Component } from 'react';
import Timeline from './monitor/Timeline.js';
import Timeseries from './monitor/Timeseries.js';
import Controlpanel from './monitor/Controlpanel.js';
import { Grid, Row, Col } from 'react-bootstrap';
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'

export default class Monitor extends Component {

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
        components.forEach(it => !!it && it.changeData(data) )
    }

    render() {
        let task_url = '/task.json'
        let solutions_url = '/task/solutions.json'
        let startUrl = '/task/start-solver'
        let stopUrl = '/task/stop-solver'

        return (
            <Grid fluid={true}>
                <Row>
                    <Col lgOffset={1} lg={10} >
                        <Controlpanel ref="controlpanel" url={task_url} start={startUrl} stop={stopUrl}/>
                        <Timeline ref='timeline' url={task_url} />
                        <Timeseries ref='timeseries' url={solutions_url} />
                    </Col>
                </Row>
            </Grid>
        )
    }
}
