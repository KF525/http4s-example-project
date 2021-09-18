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
            firstAuthor: null,
            firstLine: null,
            secondLine: ""
        };
        this.handleChange = this.handleChange.bind(this);
    }

    componentDidMount() {
        fetch('/line')
            .then(res => res.json())
            .then(
                (result) => {
                    this.setState({
                        isLoaded: true,
                        firstLine: result.line.text,
                        firstAuthor: result.poem.author.name
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

    saveCompoundPoem() {
        console.log('this is the current state:', this.state);
        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                firstLine: this.state.firstLine,
                secondLine: this.state.secondLine,
                firstAuthor: this.state.firstAuthor,
                secondAuthor: "tada"
            })
        };

        fetch('/compound', requestOptions)
            .then(response => console.log(response.json()));
            //.then(data => setPostId(data.id));
    }

    handleChange({ target }) {
        this.setState({
            [target.name]: target.value
        });
    }

    render() {
        const {error, isLoaded, firstLine, firstAuthor, secondLine} = this.state;
        if (error) {
            return <div>Error: {error.message}</div>;
        } else if (!isLoaded) {
            return <div>Loading...</div>;
        } else {
            return (
                <div>
                    <div><strong>Line:</strong>{ firstLine }</div>
                    <input
                        type="text"
                        name="secondLine"
                        placeholder="Write "
                        value={ this.state.secondLine }
                        onChange={ this.handleChange }
                    />
                    <button onClick={() => this.saveCompoundPoem()}>
                        Save
                    </button>
                </div>
            );
        }
    }
}

export default App;
