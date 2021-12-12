export async function savePoem(title, firstLine, secondLine, firstAuthor, secondAuthor) {
  const requestOptions = {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
      title,
      firstLine,
      secondLine,
      firstAuthor,
      secondAuthor
    })
  };

  const response = await fetch('/compound', requestOptions)
  return await response.json()
}

export async function getPoems() {
  const requestOptions = {
    method: 'GET',
    headers: {'Content-Type': 'application/json'},
  }

  const response = await fetch('/compound', requestOptions)
  return await response.json()
}

export const fetchLine = async () => {
  try {
    const res = await fetch('/prompt')
    const result = await res.json()
    return {
      firstLine: result.line.text,
      firstAuthor: result.poem.author.name,
      promptTitle: result.poem.title //TODO: add title
    }
  } catch (error) {
    return {error: true}
  }
}