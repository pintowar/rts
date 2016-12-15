import vis from 'vis'
import 'vis/dist/vis.css'
import React, { Component, PropTypes } from 'react'
import { Row, Col, Label, Panel, Button, ButtonGroup } from 'react-bootstrap';
import assign from 'lodash/assign'
import Immutable from 'immutable'

const eventPropTypes = {}
const eventDefaultProps = {}

export default class Timeline extends Component {

  constructor(args) {
    super(args)
    this.state = {items: [],
            groups: [],
            options: this.props.options,
            running: false,
            maxHours: -1,
            varsion: -1};
  }

  componentWillUnmount() {
    this.TimelineElement.destroy()
  }

  eventListener(channel, event) {
    if(channel && event) {
      let source = new EventSource(channel);
      source.addEventListener(event, function(e) {
        let val = JSON.parse(e.data)
        val.running = true
//        console.log(val)
        this.setState(val)
      }.bind(this), false)
    }
  }

  componentDidMount() {
    this.init()
    this.eventListener(this.props.channel, this.props.event)
    fetch(this.props.url)
              .then(r => r.json().then(s => this.setState(s)) )
              .catch(error => console.error('Error connecting to server: ' + error));
    this.TimelineElement.fit()
  }

  componentDidUpdate() {
    this.init()
  }

  shouldComponentUpdate(nextProps, nextState) {
    const {
      items, groups, options, running, maxHours, version
    } = this.state

    const itemsChange = !Immutable.fromJS(items).equals(Immutable.fromJS(nextState.items))
    const groupsChange = !Immutable.fromJS(groups).equals(Immutable.fromJS(nextState.groups))
    const optionsChange = !Immutable.fromJS(options).equals(Immutable.fromJS(nextState.options))
    const runningChange = running !== nextState.running
    const maxHoursChange = maxHours !== nextState.maxHours
    const versionChange = version !== nextState.version

    let gonnaChange = itemsChange || groupsChange || optionsChange || runningChange || maxHoursChange || versionChange

    return gonnaChange
  }

  init() {
    const { container } = this.refs
    let $el = this.TimelineElement

    const {
      items, groups, options
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
      if (groupExists) {
        timelineGroups.clear()
        timelineGroups.add(groups)
        $el.setGroups(timelineGroups)
      }
      const height = 50 * (1 + groups.length)
      options.height = height + 'px'
      $el.setOptions(options)

//      $el.fit()

      // let updatedOptions

      // If animate option is set, we should animate the timeline to any new
      // start/end values instead of jumping straight to them
      // if (animate) {
      //   updatedOptions = omit(options, 'start', 'end')
      //   $el.setWindow(options.start, options.end, { animation: animate })
      // }

      // $el.setOptions(updatedOptions)

    } else {
      $el = this.TimelineElement = new vis.Timeline(container, timelineItems, options)
      if (groupExists) {
        $el.setGroups(timelineGroups)
      }
    }

  }

  render() {
    let solverAction = (url, mode) => fetch(url).then(r => r.json().then(function(s) {
                                            var val = this.state
                                            val.running = mode
                                            this.setState(val)
                                          }.bind(this)))
                                          .catch(function(error){
                                            var val = this.state
                                            val.running = !mode
                                            this.setState(val)
                                            console.error('Error connecting to server: ' + error)
                                          }.bind(this));
    let startAction = () => solverAction(this.props.start, true)
    let stopAction = () => solverAction(this.props.stop, false)

    const buttons = (
        <Row>
        <Col xs={12} md={8} >
            <ButtonGroup>
                <Button bsStyle="success" bsSize="small" onClick={startAction} >Start Solving</Button>
                <Button bsStyle="danger" bsSize="small" onClick={stopAction} >Stop Solving</Button>
            </ButtonGroup>
        </Col>
        <Col xs={6} md={4} >
            <Label bsStyle="primary">{'Fitness: ' + this.state.maxHours}</Label>
            <Label bsStyle="info">{'Instance: ' + this.state.version}</Label>
            <Label bsStyle={this.state.running ? 'success' : 'danger'}>{this.state.running ? 'Running' : 'Stop'}</Label>
        </Col>
        </Row>
    )

    return (
        <Panel header="Task Planner" footer={buttons}>
            <div ref='container' />
        </Panel>
    )
  }
}

Timeline.propTypes = assign({
  items: PropTypes.array,
  groups: PropTypes.array,
  options: PropTypes.object,
}, eventPropTypes)

Timeline.defaultProps = assign({
  items: [],
  groups: [],
  options: {},
}, eventDefaultProps)
