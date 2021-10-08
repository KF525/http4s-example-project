import {Component} from "react";
import {fetchLine, savePoem} from "./PoemApi";

export class PoemPrompt extends Component {

  constructor(props) {
    super(props);
    this.state = {
      saving: false,
      firstLine: null,
      firstLineLoaded: false,
      firstAuthor: null,
      secondLine: ""
    };
  }

  componentDidMount = () => this.getPrompt()

  getPrompt = async () => {
    const {error, firstLine, firstAuthor} = await fetchLine()
    if (error) {
      this.setState({firstLineLoaded: false})
    } else {
      this.setState({firstLineLoaded: true, firstLine, firstAuthor})
    }
  }

  async doSaveAndUpdate() {
    this.setState({saving: true})

    const {firstAuthor, secondLine, firstLine} = this.state;
    const savedPoem = await savePoem(firstLine, secondLine, firstAuthor)
    await this.getPrompt()
    this.props.addCompoundPoem(savedPoem)
    this.setState(() => {
      return {saving: false, secondLine: ""}
    })
  }

  render() {
    if (!this.state.firstLineLoaded || this.state.saving) {
      return <div>Getting a prompt....</div>
    } else {
      return <div>
        <div><strong>Line:</strong>{this.state.firstLine}</div>
        <input
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