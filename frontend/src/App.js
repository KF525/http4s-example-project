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
      secondLine: "",
      savedLines: []
    };
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

  async saveCompoundPoem() {
    console.log('this is the current state:', this.state);
    const requestOptions = {
      method: 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify({
        firstLine: this.state.firstLine,
        secondLine: this.state.secondLine,
        firstAuthor: this.state.firstAuthor,
        secondAuthor: "tada"
      })
    };

    const response = await fetch('/compound', requestOptions)
    const savedPoem = await response.json()
    this.setState((prevState) => {
      return {savedLines: [...prevState.savedLines, savedPoem]}
    })
  }

  render() {
    const {error, isLoaded, firstLine, firstAuthor, secondLine} = this.state;
    if (error) {
      return <div>Error: {error.message}</div>;
    } else if (!isLoaded) {
      return <div>Loading...</div>;
    } else {
      return (
        <>
          <div>
            {this.state.savedLines.map((poem, i) => <div key={i}>{poem.firstLine.line.text}<br />{poem.secondLine.line.text}</div>)}
          </div>
          <div>
            <div><strong>Line:</strong>{firstLine}</div>
            <input
              type="text"
              name="secondLine"
              placeholder="Write "
              value={this.state.secondLine}
              onChange={(event) => this.setState({secondLine: event.target.value})}
            />
            <button onClick={() => this.saveCompoundPoem()}>
              Save
            </button>
          </div>
        </>
      );
    }
  }
}

export default App;
