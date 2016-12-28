import vis from 'vis'
import React, { Component } from 'react'
import { Panel } from 'react-bootstrap';
//import Immutable from 'immutable'

export default class Timeseries extends Component {

    constructor(args) {
        super(args)
        this.state = { data: {} };
        this.running = true
    }

    changeData(data) {
        this.setState({data: data})
    }

    componentDidMount() {
        const { container } = this.refs
        let self = this
        let options = {};
        let newGraph = (c => self.graph = new vis.Graph2d(container, self.dataset, options))
        this.dataset = new vis.DataSet(this.state.data)

        fetch(this.props.url)
        .then(r => r.json().then(s => self.dataset.add(s) ))
        .then(newGraph)
        .catch(newGraph)
    }

    componentDidUpdate() {
        let point = { x: this.state.data.createdAt, y: this.state.data.maxHours }
        if(!this.running && this.state.data.running) {
            this.dataset.clear()// = new vis.DataSet()
        } else {
            this.dataset.add(point)
        }

        this.graph.fit()
        this.running = this.state.data.running
    }

    render() {
        return (
            <Panel header="Planner Evolution" >
                <div ref='container' />
            </Panel>
        )
    }
}