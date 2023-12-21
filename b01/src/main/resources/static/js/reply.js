async function get1(bno){
    const result = await axios.get(`/replies/list/${bno}`);
    /*console.log(result)*/
    return result.data;
}

async function getList({bno, page, size, goLast}){
    const result = await axios.get(`/replies/list/${bno}`,
        {params: {page, size}})

    if(goLast){
        const total = result.data.total;
/*        const lastPage = parseInt(Math.ceil(total/size));*/
/* 댓글이 asc인 경우 가장 최근 댓글을 보려면 위와 같이 lastPage를 보여줄 수 있지만 난 desc로 해서 1페이지를 보여줘야함..*/
        const lastPage = 1;
        return getList({bno:bno, page:lastPage, size:size})
    }
    return result.data
}

async function addReply(replyObj){
    const response = await axios.post(`/replies/`, replyObj);
    return response.data;
}

async function getReply(rno){
    const response = await axios.get(`/replies/${rno}`)
    return response.data
}

async function modifyReply(replyObj){
    const response = await axios.put(`/replies/${replyObj.rno}`, replyObj)
    return response.data
}

async function removeReply(rno){
    const response = await axios.delete(`/replies/${rno}`)
    return response.data
}