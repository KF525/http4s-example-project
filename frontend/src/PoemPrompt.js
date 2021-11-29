import {Component} from "react";
import {fetchLine, savePoem} from "./PoemApi";
import './PoemPrompt.css';

export class PoemPrompt extends Component {

  constructor(props) {
    super(props);
    this.state = {
      saving: false,
      promptLoading: true,
      firstLine: null,
      firstAuthor: null,
      secondLine: ""
    };
  }

  componentDidMount = () => this.getPrompt()

  getPrompt = async () => {
    this.setState({promptLoading: true})
    const {error, firstLine, firstAuthor} = await fetchLine()
    if (!error) {
      this.setState({promptLoading: false, firstLine, firstAuthor})
    }
  }

  //arrow functions automatically get "this" bound
  doSaveAndUpdate = async () => {
    this.setState({saving: true})
    const {firstAuthor, secondLine, firstLine} = this.state;
    await savePoem(firstLine, secondLine, firstAuthor)
    await this.getPrompt()
    this.setState({saving: false, secondLine: ""})
    await this.props.getCompoundPoems()
  };

  render() {
    if (this.state.promptLoading || this.state.saving) {
      return <div>Getting a prompt....</div>
    } else {
      return <div className="PoemPrompt">
        <div className="line"><strong>Line:</strong>{this.state.firstLine}</div>
        <input className="input"
          type="text"
          name="secondLine"
          placeholder="Write "
          value={this.state.secondLine}
          onChange={(event) => this.setState({secondLine: event.target.value})}
        />
        <button onClick={this.doSaveAndUpdate}>Save</button>
      </div>
    }
  }
}