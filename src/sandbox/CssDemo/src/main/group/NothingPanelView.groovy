package group

application(title:'NothingPanel',
  //size:[320,480],
  pack:true,
  //location:[50,50],
  locationByPlatform:true,
  show:true,
  iconImage: imageIcon('/griffon-icon-48x48.png').image,
  iconImages: [imageIcon('/griffon-icon-48x48.png').image,
               imageIcon('/griffon-icon-32x32.png').image,
               imageIcon('/griffon-icon-16x16.png').image]
) {
    // add content here
    label('Content Goes Here') // deleteme
}
