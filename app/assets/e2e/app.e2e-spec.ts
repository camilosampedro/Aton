import { AssetsPage } from './app.po';

describe('assets App', function() {
  let page: AssetsPage;

  beforeEach(() => {
    page = new AssetsPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
