import vis from 'vis'
import React, { Component } from 'react'
import { Panel } from 'react-bootstrap';
//import Immutable from 'immutable'

export default class Timeseries extends Component {

    constructor(args) {
        super(args)
        this.state = { data: {} };
    }

    changeData(data) {
        this.setState({data: data})
    }

    componentDidMount() {
        const { container } = this.refs
        let self = this
        this.dataset = new vis.DataSet(this.state.data);
        let options = {};
        fetch(this.props.url)
        .then(r => r.json().then(function(s) {
            self.dataset.add(s)
        }))
        .then( r => self.graph = new vis.Graph2d(container, self.dataset, options) )
        .catch( c => self.graph = new vis.Graph2d(container, self.dataset, options) )
    }

    componentDidUpdate() {
        let point = { x: this.state.data.createdAt, y: this.state.data.maxHours }
        this.dataset.add(point)
        this.graph.fit()
    }

    render() {
        return (
            <Panel header="Planner Evolution" >
                <div ref='container' />
            </Panel>
        )
    }
}