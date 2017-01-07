import vis from 'vis'
import 'vis/dist/vis.css'
import '../../css/item.css'
import React, { Component } from 'react'
import { Panel } from 'react-bootstrap';
import Immutable from 'immutable'

export default class Timeline extends Component {

    constructor(args) {
        super(args)
        this.state = {items: [],
                      groups: [],
                      options: this.props.options,
                      running: false,
                      maxHours: -1,
                      version: -1};
    }

    componentWillUnmount() {
        this.TimelineElement.destroy()
    }

    changeData(data) {
        this.setState(data)
    }

    componentDidMount() {
        this.init()
        let self = this
        fetch(this.props.url)
                .then(r => r.json().then(s => self.setState(s)) )
                .catch(error => console.error('Error connecting to server: ' + error));
        this.TimelineElement.fit()
    }

    componentDidUpdate() {
        this.init()
    }

    shouldComponentUpdate(nextProps, nextState) {
        const {
//            items, groups, options, running, maxHours, version
            running, maxHours, version, currentTime
        } = this.state

//        const itemsChange = !Immutable.fromJS(items).equals(Immutable.fromJS(nextState.items))
//        const groupsChange = !Immutable.fromJS(groups).equals(Immutable.fromJS(nextState.groups))
//        const optionsChange = !Immutable.fromJS(options).equals(Immutable.fromJS(nextState.options))
        const runningChange = running !== nextState.running
        const maxHoursChange = maxHours !== nextState.maxHours
        const versionChange = version !== nextState.version
        const timeChange = currentTime !== nextState.currentTime

        //let gonnaChange = itemsChange || groupsChange || optionsChange || runningChange || maxHoursChange || versionChange

        return runningChange || maxHoursChange || versionChange || timeChange
    }

    init() {
        const { container } = this.refs
        let $el = this.TimelineElement

        const {
            items, groups, options, currentTime
            // animate = true,
        } = this.state

        const timelineItems = new vis.DataSet(items)
        const timelineGroups = new vis.DataSet(groups)
        const groupExists = groups.length > 0
        const timelineExists = !!$el

        if (timelineExists) {
            timelineItems.clear()
            timelineItems.add(items)
            $el.setItems(timelineItems)
            $el.setCurrentTime(currentTime)
            if (groupExists) {
                timelineGroups.clear()
                timelineGroups.add(groups)
                $el.setGroups(timelineGroups)
            }
            const height = 60 + 40 * (groups.length)
            options.height = height + 'px'
            $el.setOptions(options)

        } else {
            $el = this.TimelineElement = new vis.Timeline(container, timelineItems, options)
            if (groupExists) {
                $el.setGroups(timelineGroups)
            }
            $el.fit()
        }

    }

    render() {
        return (
            <Panel header="Task Planner" >
                <div ref='container' />
            </Panel>
        )
    }
}
