export function isNullFreelancer(profile) {
    let isNull = true;
    for (let key in profile) {
        if (profile[key] && key !== 'id' && key !== 'link') isNull = false;
    }

    return isNull;
}

export function generateLink(profile) {
    const link = `${window.location.origin}/freelancers/${profile.link}`;
    return link;
}