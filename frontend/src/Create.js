import {Component} from "react";
import {fetchLine, savePoem} from "./PoemApi";
import './Create.css';

export class Create extends Component {

  constructor(props) {
    super(props);
    this.state = {
      saving: false,
      promptLoading: true,
      firstLine: null,
      firstAuthor: null,
      promptTitle: null,
      title: "",
      secondLine: "",
      secondAuthor: ""
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

  doSaveAndUpdate = async () => {
    this.setState({saving: true})
    const {title, firstAuthor, secondLine, firstLine, secondAuthor} = this.state;
    await savePoem(title, firstLine, secondLine, firstAuthor, secondAuthor)
    await this.getPrompt()
    this.setState({saving: false, title: "Untitled", secondLine: "", secondAuthor: ""})
  };

  render() {
    if (this.state.promptLoading || this.state.saving) {
      return <div>Getting a prompt....</div>
    } else {
      return <div className="PoemPrompt">
        <div className="header">Create Compound Poem</div>
        <input
          type="text"
          name="title"
          placeholder="Untitled"
          value={this.state.title}
          onChange={(event) => this.setState({title: event.target.value})}
        />
        <div className="line">{this.state.firstLine}<span className="asterisk">*</span></div>
        <input className="input"
          type="text"
          name="secondLine"
          placeholder="WRITE YOUR LINE"
          value={this.state.secondLine}
          onChange={(event) => this.setState({secondLine: event.target.value})}
        />
        <input
          type="text"
          name="author"
          placeholder="Author"
          value={this.state.secondAuthor}
          onChange={(event) => this.setState({secondAuthor: event.target.value})}
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