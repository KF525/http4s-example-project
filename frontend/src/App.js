import './App.css';
import {Component} from "react";
import {CompoundPoem} from "./CompoundPoem";
import {PoemPrompt} from "./PoemPrompt";
import {getPoems} from "./PoemApi";

class App extends Component {

  constructor(props) {
    super(props);
    this.state = {
      error: null,
      savedPoems: [],
    };
  }

  componentDidMount = () => this.getCompoundPoems()

  getCompoundPoems = async () => {
    const savedPoems = await getPoems()
    console.log(savedPoems)
    this.setState( {savedPoems})
  }

  render() {
    const {error} = this.state;
    if (error) {
      return <div>Error: {error.message}</div>;
    } else {
      return (
        <>
          <div>
            {this.state.savedPoems.map((poem, i) => <CompoundPoem key={i} poem={poem}/>)}
          </div>
          <hr/>
          <PoemPrompt/>
        </>
      );
    }
  }
}

export default App;
