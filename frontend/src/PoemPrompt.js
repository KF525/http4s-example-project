import {Component} from "react";

export class PoemPrompt extends Component {

  constructor(props) {
    super(props);
    this.state = {
      saving: false,
      firstLine: null,
      firstLineLoaded: false,
      secondLine: ""
    };
  }

  componentDidMount = () => this.getPrompt()

  getPrompt = () =>
    fetch('/line')
      .then(res => res.json())
      .then(
        (result) => {
          this.setState({
            firstLineLoaded: true,
            firstLine: result.line.text,
            firstAuthor: result.poem.author.name
          });
        },
        // Note: it's important to handle errors here
        // instead of a catch() block so that we don't swallow
        // exceptions from actual bugs in components.
        (error) => {
          this.setState({
            firstLineLoaded: false,
            error
          });
        }
      );

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
        <button onClick={() => this.saveCompoundPoem()}>
          Save
        </button>
      </div>;
    }
  }
}