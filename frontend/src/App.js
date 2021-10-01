import './App.css';
import {Component} from "react";
import {CompoundPoem} from "./CompoundPoem";
import {PoemPrompt} from "./PoemPrompt";

class App extends Component {

  constructor(props) {
    super(props);
    this.state = {
      error: null,
      savedLines: [],
    };
  }

  addCompoundPoem = (savedPoem) => {
    this.setState((prevState) => {
      return {savedLines: [...prevState.savedLines, savedPoem]}
    })
  }

  render() {
    const {error} = this.state;
    if (error) {
      return <div>Error: {error.message}</div>;
    } else {
      return (
        <>
          <div>
            {this.state.savedLines.map((poem, i) => <CompoundPoem key={i} poem={poem}/>)}
          </div>
          <hr/>
          <PoemPrompt addCompoundPoem={this.addCompoundPoem}/>
        </>
      );
    }
  }
}

export default App;
