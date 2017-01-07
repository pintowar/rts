import vis from 'vis'
import React, { Component } from 'react'
import { Panel } from 'react-bootstrap';
//import Immutable from 'immutable'

export default class Timeseries extends Component {

    constructor(args) {
        super(args)
        this.state = { data: {} };
        this.running = true
        this.dataset = new vis.DataSet(this.state.data)
    }

    changeData(data) {
        this.setState({data: data})
    }

    componentDidMount() {
        let self = this
        this.graph = new vis.Graph2d(this.refs.container, this.dataset, {})

        fetch(this.props.url)
        .then(r => r.json().then(s => self.dataset.add(s) ))
        .then(c => self.graph.fit() )
        .catch(c => self.graph.fit() )
    }

    componentDidUpdate() {
        let point = { x: this.state.data.createdAt, y: this.state.data.maxHours }
        if(this.running !== undefined && !this.running && this.state.data.running) {
            this.dataset.clear()// = new vis.DataSet()
        } else {
            if(point.x) this.dataset.add(point)
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
