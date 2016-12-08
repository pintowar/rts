import vis from 'vis'
import 'vis/dist/vis.css'
import React, { Component, PropTypes } from 'react'
import assign from 'lodash/assign'
import Immutable from 'immutable'

const eventPropTypes = {}
const eventDefaultProps = {}

export default class Timeline extends Component {

  constructor(args) {
    super(args)
    this.state = {items: [],
            groups: [],
            options: this.props.options};
  }

  componentWillUnmount() {
    this.TimelineElement.destroy()
  }

  eventListener(channel, event) {
    if(channel && event) {
      let source = new EventSource(channel);
      source.addEventListener(event, function(e) {
        let val = JSON.parse(e.data)
        //console.log(val)
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
  }

  componentDidUpdate() {
    this.init()

  }

  shouldComponentUpdate(nextProps, nextState) {
    const {
      items, groups, options
    } = this.state

    const itemsChange = !Immutable.fromJS(items).equals(Immutable.fromJS(nextState.items))
    const groupsChange = !Immutable.fromJS(groups).equals(Immutable.fromJS(nextState.groups))
    const optionsChange = !Immutable.fromJS(options).equals(Immutable.fromJS(nextState.options))

    let gonnaChange = itemsChange || groupsChange || optionsChange

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
    return <div ref='container' />
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
