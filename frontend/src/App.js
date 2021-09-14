import logo from './logo.svg';
import './App.css';
import {Component} from "react";

class App extends Component {
    //initialize component state - controlled way of managing component. "local database" - component reacts to state
    //constructor -> render (1+) -> componentDidMount (takes x amount of time) as soon as isLoaded true, render will display
    //never edit state in render
    //componentDidMount safe to edit state

    constructor(props) {
        super(props);
        this.state = {
            error: null,
            isLoaded: false,
            line: null
        };
    }

    componentDidMount() {
        fetch('/line')
            .then(res => res.json())
            .then(
                (result) => {
                    this.setState({
                        isLoaded: true,
                        line: result.line
                    });
                },
                // Note: it's important to handle errors here
                // instead of a catch() block so that we don't swallow
                // exceptions from actual bugs in components.
                (error) => {
                    this.setState({
                        isLoaded: true,
                        error
                    });
                }
            )
    }

    render() {
        const {error, isLoaded, line} = this.state;
        if (error) {
            return <div>Error: {error.message}</div>;
        } else if (!isLoaded) {
            return <div>Loading...</div>;
        } else {
            return (
                <div>
                    <div><strong>Line:</strong>{line.text}</div>
                    <input/>
                </div>
            );
        }
    }
}

export default App;
