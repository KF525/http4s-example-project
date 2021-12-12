import './Read.css';
import {Component} from "react";
import {CompoundPoem} from "./CompoundPoem";
import {getPoems} from "./PoemApi";

class Read extends Component {

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
        </>
      );
    }
  }
}

export default Read;
