import './App.css';
import {Component} from "react";
import {CompoundPoem} from "./CompoundPoem";

class App extends Component {

  constructor(props) {
    super(props);
    this.state = {
      error: null,
      isLoaded: false,
      firstAuthor: null,
      firstLine: null,
      secondLine: "",
      savedLines: [],
      saving: false
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
    this.setState({saving: true})
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
    }, () => this.setState({saving: false}))
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
            {this.state.savedLines.map((poem, i) => <CompoundPoem key={i} poem={poem}/>)}
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
            <button onClick={() => this.saveCompoundPoem()} disabled={this.state.saving}>
              Save
            </button>
          </div>
        </>
      );
    }
  }
}

export default App;
