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
      promptTitle: null,
      secondLine: ""
    };
  }

  componentDidMount = () => this.getPrompt()

  getPrompt = async () => {
    this.setState({promptLoading: true})
    const {error, firstLine, firstAuthor, promptTitle} = await fetchLine()
    if (!error) {
      this.setState({promptLoading: false, firstLine, firstAuthor, promptTitle})
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
        <div className="header">Create Compound Poem</div>
        <div className="line">{this.state.firstLine}<span className="asterisk">*</span></div>
        <input className="input"
          type="text"
          name="secondLine"
          placeholder="WRITE YOUR LINE"
          value={this.state.secondLine}
          onChange={(event) => this.setState({secondLine: event.target.value})}
        />
        <div className="buttons">
          <button className="savePoem" onClick={this.doSaveAndUpdate}>Save Poem</button>
          <button className="newPrompt" onClick={this.getPrompt}>New Prompt</button>
        </div>
        <div className="source">*From {this.state.firstAuthor}'s <span className="title">Placeholder Title</span></div>
      </div>
    }
  }
}