import React, { Component } from 'react'
import { Link } from 'react-router'
import { Image, Navbar } from 'react-bootstrap'
import goes from './images/logo_goes.png'

export default class App extends Component {

    render() {
        return (
            <div id="parent">
                <Navbar>
                    <Navbar.Header>
                        <Navbar.Brand><Image src={goes} /></Navbar.Brand>
                        <Navbar.Toggle />
                    </Navbar.Header>
                    <Navbar.Collapse>
                        <Navbar.Text><Link to="/monitor">Monitor</Link></Navbar.Text>
                        <Navbar.Text><Link to="/about">About</Link></Navbar.Text>
                        <Navbar.Text pullRight>Task Planner</Navbar.Text>
                    </Navbar.Collapse>
                </Navbar>
                {this.props.children}
            </div>
        )
    }
}
