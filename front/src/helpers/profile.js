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

export function getUserName(profile) {
    if (!profile.first_name && !profile.last_name) return 'Имя Фамилия';
    return `${profile.first_name} ${profile.last_name}` 
}