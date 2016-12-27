import React, { Component } from 'react'
import { Row, Col, Label, Panel, Button, ButtonGroup } from 'react-bootstrap';

export default class Controlpanel extends Component {

    constructor(args) {
        super(args)
        this.state = {items: [],
                groups: [],
                options: this.props.options,
                running: false,
                maxHours: -1,
                varsion: -1};
    }

    changeData(data) {
        this.setState(data)
    }

    solve(url, mode) {
        let self = this
        fetch(url).then(r => r.json().then(s => self.setState(s)))
                                     .catch(function(error) {
                                          var val = self.state
                                          val.running = !mode
                                          //self.setState(val)
                                          console.error('Error connecting to server: ' + error)
                                     });
    }

    startAction() {
        this.solve(this.props.start, true)
    }

    stopAction() {
        this.solve(this.props.stop, true)
    }

    render() {
        let running = this.state.running
        return (
            <Panel header="Control Panel">
                <Row>
                    <Col xs={12} md={8} >
                        <ButtonGroup>
                            <Button bsStyle="success" bsSize="small" onClick={this.startAction.bind(this)} >Start Solving</Button>
                            <Button bsStyle="danger" bsSize="small" onClick={this.stopAction.bind(this)} >Stop Solving</Button>
                        </ButtonGroup>
                    </Col>
                    <Col xs={6} md={4} >
                        <Label bsStyle="primary">{'Fitness: ' + this.state.maxHours}</Label>
                        <Label bsStyle="info">{'Instance: ' + this.state.version}</Label>
                        <Label bsStyle={running ? 'success' : 'danger'}>{running ? 'Running' : 'Stop'}</Label>
                    </Col>
                </Row>
            </Panel>
        )
    }
}
