export async function savePoem(firstLine, secondLine, firstAuthor) {
  const requestOptions = {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({
      firstLine,
      secondLine,
      firstAuthor,
      secondAuthor: "tada"
    })
  };

  const response = await fetch('/compound', requestOptions)
  return await response.json()
}

export const fetchLine = async () => {
  try {
    const res = await fetch('/line')
    const result = await res.json()
    return {
      firstLine: result.line.text,
      firstAuthor: result.poem.author.name
    }
  } catch (error) {
    return {error: true}
  }
}