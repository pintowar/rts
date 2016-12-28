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
                version: -1}
    }

    changeData(data) {
        this.setState(data)
    }

    solve(url) {
        let parent = this._reactInternalInstance._currentElement._owner._instance
        fetch(url).then(r => r.json().then(s => parent.changeData(s)))
                  .catch(error => console.error('Error connecting to server: ' + error) )
    }

    componentDidMount() {
        let self = this
        fetch(this.props.url)
                .then(r => r.json().then(s => self.setState(s)) )
                .catch(error => console.error('Error connecting to server: ' + error))
    }

    start() {
        let parent = this._reactInternalInstance._currentElement._owner._instance
        let url = this.props.start
        fetch(url).then(r => r.json().then(s => parent.changeData(s)))
                  .catch(error => console.error('Error connecting to server: ' + error) )
    }

    stop() {
        let self = this
        let url = this.props.stop
        fetch(url).then(r => r.json().then(s => self.setState(s)))
                  .catch(error => console.error('Error connecting to server: ' + error) )
    }

    render() {
        let running = this.state.running
        return (
            <Panel header="Control Panel">
                <Row>
                    <Col xs={12} md={8} >
                        <ButtonGroup>
                            <Button bsStyle="success" bsSize="small" onClick={this.start.bind(this)} >Start Solving</Button>
                            <Button bsStyle="danger" bsSize="small" onClick={this.stop.bind(this)} >Stop Solving</Button>
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
